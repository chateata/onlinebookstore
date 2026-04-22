### 项目概述

这是一个完整的在线书店管理系统，严格按照功能需求实现了6大核心功能模块：供书目录及库存管理、采购管理、客户管理、顾客订单管理和发货管理、供应商管理、网上浏览查询。

**核心功能模块**：
1. **供书目录及库存管理** - 完整的书籍信息管理、分类体系、库存控制和丛书管理
2. **采购管理** - 缺书登记管理、采购单管理和自动库存补充
3. **客户管理** - 用户注册登录、信用等级管理和账户余额系统
4. **顾客订单管理和发货管理** - 完整的订单生命周期管理和发货流程
5. **供应商管理** - 多供应商体系和供应商书目管理
6. **网上浏览查询** - 多维度书籍查询和客户信息查询

技术栈为：Spring Boot 2.2.7 + Thymeleaf（前端模板） + MyBatis 2.1.2（持久层） + MySQL 8.0；连接池 Druid 1.1.10，分页 PageHelper 1.2.3。前端采用Layui组件库，数据库包含35+张表，支持完整的业务关系和数据完整性约束。

包结构以 com.shop.bookshop 为根，按职责分层：
- controller (Web 层)
- service (业务层)
- dao (持久层接口 / MyBatis mapper)
- pojo (实体)
- config/interceptor/exception/util（配置/拦截/异常/工具）。

其他资源包括：src/main/resources/templates（Thymeleaf 页面）、static（静态资源）、mappers（MyBatis XML 映射）。

数据库设计包含多张核心表，支持完整的业务关系：
- **基础数据表**：user_info（用户信息）、admin（管理员）、credit_level（信用等级）
- **书籍相关表**：book（书籍信息）、category（分类）、author（作者）、publisher（出版社）、series（丛书系列）、keyword（关键字）
- **关联关系表**：book_author（书籍-作者关联）、book_keyword（书籍-关键字关联）
- **订单相关表**：order_info（订单主表）、order_item（订单明细）
- **购物车表**：shopping_cart（购物车）
- **供应商体系**：supplier（供应商）、supplier_book（供应商书目）、book_supplier（书籍供应商关联）
- **采购管理**：shortage（缺书记录）、purchase_order（采购单）、purchase_order_item（采购单明细）
- **其他业务表**：user_center（用户中心）等

#### 目录/文件逐项说明

- 项目根
  - `pom.xml`：Maven 构建与依赖。
  - `Readme.md`：运行/配置说明、初始化 DB 的步骤与默认路由。

- src/main/java/com/shop/bookshop
  - `controller/`：HTTP 请求处理器（接收请求、校验参数、调用 service、返回视图或 JSON）。
    - 包括：`AdminController`, `AdminRouterController`, `BookController`, `BookDisplayController`, `CategoryController`, `ClientRouterController`, `CreditLevelController`, `ImageUploadController`, `LoginRegisterController`, `OrderController`, `PurchaseController`, `SeriesController`, `ShoppingCartController`, `SupplierController`, `UserCenterController`, `UserController` 等。
    - 页面控制（返回视图）与接口（返回 JSON）并存。
  - `service/`：封装业务逻辑，controller 调用 service；实现类在 `service/impl`，接口在 `service` 根目录。
    - 包括：`AdminService`, `BookDisplayService`, `BookService`, `CategoryService`, `LoginRegisterService`, `OrderHandleService`, `OrderService`, `PurchaseService`, `ShoppingCartService`, `UserService` 等。
    - Service 会调用 DAO 操作数据库、进行事务/业务校验。
  - `dao/`：MyBatis 接口（Mapper），接口方法与 `resources/mappers/*.xml` 中的 SQL 映射对应。MyBatis 将这些接口代理为实现。
  - `pojo/`：实体类（Admin, Author, Book, BookAuthor, BookKeyword, BookSupplier, Category, CreditLevel, Keyword, Order, OrderItem, Publisher, PurchaseOrder, PurchaseOrderItem, Series, ShoppingCart, Shortage, Supplier, SupplierBook, User）。这些类在 service/dao 之间传递，且被 Thymeleaf 模板渲染。
  - `config/`：框架/组件配置（如拦截器注册）。
  - `interceptor/`：如 `ClientLoginInterceptor`、`AdminInterceptor`，用于拦截特定路由，做权限/登录检查。
  - `exception/`：自定义异常与全局异常处理（统一返回错误信息或跳转）。
  - `util/`：工具/统一响应类，如响应代码枚举与统一响应结构。
  - `ResultCode`：定义统一的状态码与消息，配合 `ResultVO`（项目中存在）构成 API 响应约定。

- src/main/resources
  - `application.yml`：公共配置（默认激活 profile `demo`）。
  - `application-demo.yml.example`：demo 环境配置模板（**无真实密码**）；首次使用请复制为 `application-demo.yml` 并自行填写数据库用户名与密码（`application-demo.yml` 已被 Git 忽略，勿提交）。
  - `mappers/`：MyBatis 的 SQL 映射文件（`AdminMapper.xml`, `AuthorMapper.xml`, `BookDisplayMapper.xml`, `BookMapper.xml`, `CategoryMapper.xml`, `CreditLevelMapper.xml`, `KeywordMapper.xml`, `OrderItemMapper.xml`, `OrderMapper.xml`, `PublisherMapper.xml`, `PurchaseOrderItemMapper.xml`, `PurchaseOrderMapper.xml`, `SeriesMapper.xml`, `ShoppingCartMapper.xml`, `ShortageMapper.xml`, `SupplierBookMapper.xml`, `SupplierMapper.xml`, `UserMapper.xml` 等）。Mapper XML 与 `dao` 包内的接口方法同名或按 namespace 对应。
  - `templates/`：Thymeleaf 页面（`index.html`, `details.html`, `login.html`, `register.html`, `shopping_cart.html`, `user_center.html`, `user_orders.html` 等）；`admin/` 目录包含后台管理页面（`admin.html`, `books.html`, `category.html`, `inventory.html`, `login.html`, `order.html`, `purchase.html`, `supplier.html`, `user.html`, `add_book.html`）；`_fragment.html`、`_adminfragment.html` 放公共片段（页头/页尾/样式）。
  - `static/`：前端静态资源（`css/`, `js/`, `images/book_images/`, `lib/layui/` 等）。浏览器请求 `/static/...` 下的资源（Spring Boot 自动映射），模板中通过相对路径或 `@{/css/...}` 引用。
  - `static/api/*.json`：示例/测试数据（如 `book.json`），供前端开发或 mock 使用。

#### 数据映射

通过MyBatis（DAO <-> XML）实现映射关系：
- DAO 接口（`dao/BookMapper.java`）定义方法签名；对应 XML（`mappers/BookMapper.xml`）中 `<select id="methodName">` 的 id 与接口方法匹配，MyBatis 负责把查询结果映射为 POJO。
- `@MapperScan("com.shop.bookshop.dao")` 在启动类中启用自动扫描。

典型请求处理流程（以书籍详情为例）：
1. 浏览器请求 `/index/books/details/{id}`（或 controller 指定的路径）。
2. 对应 `BookDisplayController` 接收请求，调用 `BookDisplayService`（业务层）。
3. `BookDisplayService` 调用 `BookMapper`（DAO）来查询数据库（通过 MyBatis XML）。
4. DAO 返回 `Book` POJO（`pojo/Book.java`），Service 可能做额外处理后将 `book` 加入 Model。
5. Controller 返回视图名 `details`（Thymeleaf 模板 `details.html`），模板通过 `${book}` 渲染页面，静态资源从 `static/` 提供（JS/CSS）。
6. 若是 API 请求（AJAX），Controller 返回 `ResultVO`（包含 `code` 和 `msg`，使用 `ResultCode` 枚举）。


#### 认证与拦截（session 操作）
- 登录：`LoginRegisterController` 处理登录逻辑，成功后将 `user` 或 `admin` 放入 session（供后续校验使用）。
- 拦截器：`ClientLoginInterceptor` 检查 session 中是否存在 `user`，没有则返回未登录提示或重定向登录页；`AdminInterceptor` 限制 `admin` 路由。
- 拦截器通过 `config/InterceptorConfig.java` 注册并匹配路由。

#### 错误与异常处理
- 自定义异常（`CustomizeException`）用于在 service/controller 层抛出特定错误。
- `GlobalExceptionHandler` 统一捕获异常并返回标准 `ResultVO` 或错误页面。

#### 修改点与定位
- 修改数据库配置：复制 `application-demo.yml.example` 为 `application-demo.yml`，编辑其中 `spring.datasource`（勿将含密码的 `application-demo.yml` 提交到 Git）。
- 新增 SQL：在 `dao` 添加接口方法并在 `resources/mappers` 新增/修改对应 XML。
- 新增页面：在 `templates/` 添加 HTML（可使用 `_fragment` 片段），并在 `controller` 对应方法中返回视图名；静态资源放 `static/`。
- API 返回格式：修改 `util/ResultVO` 与 `util/ResultCode` 来统一前端接口规范。


---


### 运行与初始化

#### 前置依赖（需自行配置）

- JDK 8
- Apache Maven（需要本机已安装 Maven，并把 `mvn` 加到 PATH）
- MySQL 8.0（应用通过 JDBC 连库）

#### 运行和初始化

- 在你的电脑中自行导入数据库：数据库名为 `bookshop`
- **配置数据库账号（勿提交密码）**：将 `src/main/resources/application-demo.yml.example` 复制为同目录下的 `application-demo.yml`，打开后者并填写 `spring.datasource.username` 与 `spring.datasource.password`（示例文件里这两项为空字符串，仅作占位）。`application-demo.yml` 已列入 `.gitignore`，请勿把真实密码推送到远程仓库。
- 导入数据库脚本：`bookshop.sql`（包含完整的表结构、初始数据和业务逻辑）
- 运行 `BookshopApplication` 启动应用（默认已使用 `demo` profile）
- 前台用户账号（预设）：账号 `teamo` 密码 `123456`
- 后台管理员账号（预设）：账号 `admin` 密码 `123456`
- 默认运行端口：8080（可在 `application-demo.yml` 中修改 `server.port`）

**PowerShell 复制示例：**

```powershell
Copy-Item src\main\resources\application-demo.yml.example src\main\resources\application-demo.yml
# 再用编辑器打开 application-demo.yml 填写数据库用户名与密码
```

```
# 打包后运行（需已按上文生成并填写 application-demo.yml 且重新打包）
# 删除上一次构建产物，编译代码并打包，跳过测试的执行
mvn clean package -DskipTests
# 直接运行上一步生成的可执行 jar，启动 Spring Boot 应用，并指定启用 demo 这个 Spring Profile
java -jar target\bookshop-0.0.1-SNAPSHOT.jar --spring.profiles.active=demo
```

---

### 访问地址与说明


- **默认主机与端口**：`http://localhost:8080/`（`server.port` 未配置时为 8080，`src/main/resources/application.yml` 中默认 profile 为 `demo`）
- **首页**：`http://localhost:8080/` 或 `http://localhost:8080/index`
- **用户登录页**：`http://localhost:8080/login`
- **用户注册页**：`http://localhost:8080/register`
- **书籍详情页**（示例 bookId=1）：`http://localhost:8080/index/books/details/1`
- **购物车 / 个人订单（需登录）**：
  - 购物车：`http://localhost:8080/{userName}/shopping_cart`（例如 `http://localhost:8080/admin/shopping_cart`）
  - 个人订单：`http://localhost:8080/{userName}/orders`

- **后台管理页面（需要管理员登录）**：
  - 后台登录页：`http://localhost:8080/admin/login`
  - 后台入口 / 书籍管理：`http://localhost:8080/admin/` 或 `http://localhost:8080/admin/book_manage`
  - 用户管理：`/admin/user_manage`
  - 分类管理：`/admin/category_manage`
  - 订单管理：`/admin/order_manage`
  - 添加书籍：`/admin/add_book`
  - 供应商管理：`/admin/supplier_manage`
  - 采购管理：`/admin/purchase_manage`
  - 库存管理：`/admin/inventory_manage`
  - 管理员管理：`/admin/admin_manage`


注意！
- 需要先启动并连接好 MySQL（数据库名 `bookshop`），否则页面会报数据错误。
- 数据库脚本包含完整的表结构、初始数据、触发器和存储过程，确保导入后系统正常运行。
- 如要修改端口运行：`java -jar target\bookshop-0.0.1-SNAPSHOT.jar --server.port=8090` 或在 `application.yml` 中设置 `server.port`。
- 登录约束：用户相关页面（购物车、订单）必须先通过 `/login` 页面登录并在 session 中存在 `user`；后台登录通过 `/admin/login` 页面，管理员账号 `admin` / `123456`。
- 系统支持完整的业务流程：用户注册→登录→浏览书籍→加入购物车→提交订单→管理员审核→发货，整个流程都有相应的权限控制和数据校验。

---

### 团队开发：静态检查（Checkstyle / SpotBugs）

首次启动项目不强制要求运行静态检查；但在团队协作/提交代码/CI 中建议统一执行。

#### 推荐命令（团队统一）

- **快速检查（只跑 Checkstyle，快）**

```
mvn validate
```

- **生成 Checkstyle HTML 报告**（可视化查看）

```
mvn checkstyle:checkstyle
```

- **全量检查（Checkstyle + 编译 + SpotBugs 出报告）**

```
mvn clean verify
```

- **全量检查并运行单元测试**（如需）

```
mvn clean verify -DskipTests=false
```

#### 报告路径（生成后打开）

- **Checkstyle**
  - **HTML 报告**：`target/site/checkstyle.html`
  - **控制台输出**：运行 `mvn validate` 时直接打印（当前配置为只报告，不失败）

- **SpotBugs**
  - **HTML 报告**：`target/site/spotbugs.html`
  - **XML 报告**：`target/spotbugsXml.xml`
  - **其他报告文件**：`target/spotbugs.xml`

#### Windows 下打开报告（在项目根目录执行）

```powershell
start .\target\site\checkstyle.html
start .\target\site\spotbugs.html
```

---

### 团队测试：单元测试 / 覆盖率

#### 测试分层约定（团队统一）

- **单元测试（Unit Test）**：不依赖真实数据库/网络/文件系统；主要测试 `service`/`util` 的业务逻辑与边界条件。命名以 `*UnitTest.java` 结尾。

#### 推荐命令（团队统一）

- **只跑单元测试**

```
mvn -DskipTests=false test
```

#### 覆盖率报告

- **JaCoCo HTML**：`target/site/jacoco/index.html`

