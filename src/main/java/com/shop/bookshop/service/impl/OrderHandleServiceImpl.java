package com.shop.bookshop.service.impl;

import com.github.pagehelper.PageHelper;
import com.shop.bookshop.exception.CustomizeException;
import com.shop.bookshop.dao.BookMapper;
import com.shop.bookshop.dao.OrderItemMapper;
import com.shop.bookshop.dao.OrderMapper;
import com.shop.bookshop.dao.ShoppingCartMapper;
import com.shop.bookshop.dao.UserMapper;
import com.shop.bookshop.dao.CreditLevelMapper;
import com.shop.bookshop.pojo.Book;
import com.shop.bookshop.pojo.Order;
import com.shop.bookshop.pojo.OrderItem;
import com.shop.bookshop.pojo.User;
import com.shop.bookshop.pojo.CreditLevel;
import com.shop.bookshop.service.OrderHandleService;
import com.shop.bookshop.util.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderHandleServiceImpl implements OrderHandleService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderItemMapper orderItemMapper;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private CreditLevelMapper creditLevelMapper;

    /**
     * 创建用户订单
     * @param order
     */
    @Override
    public void createOrder(Order order) {

        // 验证订单项并计算折后单价与总价（按客户信用等级折扣）
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new CustomizeException(ResultCode.FAILED, "订单项不能为空");
        }
        // 读取用户与信用等级
        User user = null;
        CreditLevel creditLevel = null;
        if (order.getUserId() != null) {
            user = userMapper.selectByUserId(order.getUserId());
            if (user != null && user.getCreditLevelId() != null) {
                creditLevel = creditLevelMapper.selectByLevelId(user.getCreditLevelId());
            }
        }
        BigDecimal discountRate = BigDecimal.ZERO;
        BigDecimal overdraftLimit = BigDecimal.ZERO;
        if (creditLevel != null) {
            discountRate = creditLevel.getDiscountRate() != null ? creditLevel.getDiscountRate() : BigDecimal.ZERO;
            overdraftLimit = creditLevel.getOverdraftLimit() != null ? creditLevel.getOverdraftLimit() : BigDecimal.ZERO;
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            Book book = bookMapper.selectByBookId(orderItem.getBookId());
            if (book == null) {
                throw new CustomizeException(ResultCode.FAILED, "书籍不存在，ID=" + orderItem.getBookId());
            }
            // 库存校验：下单时必须确保库存足够
            Integer stock = book.getStock() == null ? 0 : book.getStock();
            if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
                throw new CustomizeException(ResultCode.FAILED, "订单项数量错误，bookId=" + orderItem.getBookId());
            }
            if (stock < orderItem.getQuantity()) {
                throw new CustomizeException(ResultCode.INSUFFICIENT_STOCK, "库存不足，bookId=" + orderItem.getBookId());
            }
            // 按折扣计算单价与小计（四舍五入到2位）
            BigDecimal unitPrice = book.getPrice().multiply(BigDecimal.ONE.subtract(discountRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(orderItem.getQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP);
            orderItem.setPrice(unitPrice);  // 设置折后价格
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);
            totalAmount = totalAmount.add(subtotal);
        }
        order.setTotalAmount(totalAmount);

        // 余额/透支检查：始终校验下单是否在允许透支范围内
        BigDecimal userBalance = (user != null && user.getAccountBalance() != null) ? user.getAccountBalance() : BigDecimal.ZERO;
        BigDecimal allowedNegative = overdraftLimit.negate();
        BigDecimal after = userBalance.subtract(totalAmount);
        if (after.compareTo(allowedNegative) < 0) {
            throw new CustomizeException(ResultCode.INSUFFICIENT_CREDIT, "余额或信用不足，无法下单");
        }
        // 设置默认支付与发货状态
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("PENDING");
        }
        if (order.getShippingStatus() == null) {
            order.setShippingStatus("PENDING");
        }
        // 插入父订单（回写 orderId）
        orderMapper.insert(order);
        // 原子性地扣减库存：在同一事务内对每个订单项执行 UPDATE ... WHERE stock>=qty
        for (OrderItem orderItem : order.getOrderItems()) {
            int updated = bookMapper.decrementStockIfEnough(orderItem.getBookId(), orderItem.getQuantity());
            if (updated == 0) {
                throw new CustomizeException(ResultCode.INSUFFICIENT_STOCK, "库存不足或已被其他订单占用，bookId=" + orderItem.getBookId());
            }
        }
        // 插入订单子项并删除购物车记录
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrderId(order.getOrderId());
            // 确保 shippedQuantity 非 null（数据库列为 NOT NULL）
            if (orderItem.getShippedQuantity() == null) {
                orderItem.setShippedQuantity(0);
            }
            orderItemMapper.insert(orderItem);
            if (order.getUserId() != null) {
                shoppingCartMapper.deleteByUserIdAndBookId(order.getUserId(), orderItem.getBookId());
            }
        }
        // 如果订单为已支付状态（例如前端即时支付），则立即扣减用户余额并持久化
        if ("PAID".equalsIgnoreCase(order.getPaymentStatus()) && user != null) {
            user.setAccountBalance(userBalance.subtract(totalAmount));
            userMapper.updateByUserId(user);
        }
    }

    @Override
    public int deleteOrderById(Integer orderId) {
        orderMapper.deleteByOrderId(orderId);
        return 0;
    }

    /**
     * 查询用户订单
     * @param userId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<Order> getOrdersByUserId(Integer userId, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Order> orders = orderMapper.selectByUserId(userId);
        return orders;
    }

    /**
     * 查询全部订单
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<Order> getAllOrdersByPage(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Order> orders = orderMapper.selectAll();
        return orders;
    }

    /**
     * 发货实现：按传入的订单项进行发货（分批），扣减用户余额并更新订单/订单项状态
     */
    @Override
    public void shipOrder(Integer orderId, List<OrderItem> shipItems) {
        if (orderId == null || shipItems == null || shipItems.isEmpty()) {
            throw new CustomizeException(ResultCode.FAILED, "参数错误：订单ID或发货项为空");
        }
        Order order = orderMapper.selectByOrderId(orderId);
        if (order == null) {
            throw new CustomizeException(ResultCode.FAILED, "订单不存在，ID=" + orderId);
        }
        User user = null;
        CreditLevel creditLevel = null;
        if (order.getUserId() != null) {
            user = userMapper.selectByUserId(order.getUserId());
            if (user != null && user.getCreditLevelId() != null) {
                creditLevel = creditLevelMapper.selectByLevelId(user.getCreditLevelId());
            }
        }
        BigDecimal overdraftLimit = BigDecimal.ZERO;
        if (creditLevel != null && creditLevel.getOverdraftLimit() != null) {
            overdraftLimit = creditLevel.getOverdraftLimit();
        }
        // map existing order items by bookId
        Map<Integer, OrderItem> existingMap = new HashMap<>();
        if (order.getOrderItems() != null) {
            for (OrderItem oi : order.getOrderItems()) {
                existingMap.put(oi.getBookId(), oi);
            }
        }
        // 计算本次发货总额
        BigDecimal shipTotal = BigDecimal.ZERO;
        for (OrderItem shipItem : shipItems) {
            OrderItem exist = existingMap.get(shipItem.getBookId());
            if (exist == null) {
                throw new CustomizeException(ResultCode.FAILED, "订单项不存在，bookId=" + shipItem.getBookId());
            }
            int remain = (exist.getQuantity() == null ? 0 : exist.getQuantity()) - (exist.getShippedQuantity() == null ? 0 : exist.getShippedQuantity());
            int toShip = shipItem.getQuantity() == null ? 0 : shipItem.getQuantity();
            if (toShip <= 0) continue;
            if (toShip > remain) {
                throw new CustomizeException(ResultCode.FAILED, "发货数量超过未发数量，bookId=" + shipItem.getBookId());
            }
            BigDecimal unit = exist.getUnitPrice() == null ? exist.getPrice() : exist.getUnitPrice();
            BigDecimal part = unit.multiply(new BigDecimal(toShip)).setScale(2, BigDecimal.ROUND_HALF_UP);
            shipTotal = shipTotal.add(part);
        }
        // 支付校验：若订单未付款则需检查余额/透支
        String paymentStatus = order.getPaymentStatus() == null ? "PENDING" : order.getPaymentStatus();
        if (!"PAID".equalsIgnoreCase(paymentStatus)) {
            BigDecimal userBalance = user != null && user.getAccountBalance() != null ? user.getAccountBalance() : BigDecimal.ZERO;
            BigDecimal allowedNegative = overdraftLimit.negate();
            BigDecimal after = userBalance.subtract(shipTotal);
            if (after.compareTo(allowedNegative) < 0) {
                throw new CustomizeException(ResultCode.FAILED, "余额或信用不足，无法发货");
            }
            // 扣减余额并持久化
            if (user != null) {
                user.setAccountBalance(userBalance.subtract(shipTotal));
                userMapper.updateByUserId(user);
            }
        }
        // 执行发货：更新 order_item.shipped_quantity
        for (OrderItem shipItem : shipItems) {
            OrderItem exist = existingMap.get(shipItem.getBookId());
            int toShip = shipItem.getQuantity() == null ? 0 : shipItem.getQuantity();
            int newShipped = (exist.getShippedQuantity() == null ? 0 : exist.getShippedQuantity()) + toShip;
            exist.setShippedQuantity(newShipped);
            orderItemMapper.updateByOrderIdAndBookId(exist);
        }
        // 重新计算订单发货状态
        int totalOrdered = 0;
        int totalShipped = 0;
        for (OrderItem oi : order.getOrderItems()) {
            totalOrdered += oi.getQuantity() == null ? 0 : oi.getQuantity();
            totalShipped += oi.getShippedQuantity() == null ? 0 : oi.getShippedQuantity();
        }
        if (totalShipped >= totalOrdered) {
            order.setShippingStatus("SHIPPED");
        } else if (totalShipped > 0) {
            order.setShippingStatus("PARTIAL");
        } else {
            order.setShippingStatus("PENDING");
        }
        orderMapper.updateByOrderId(order);
    }
}
