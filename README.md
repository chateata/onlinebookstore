### 项目类型

技术栈为：Spring Boot + Thymeleaf（前端模板） + MyBatis（持久层） + MySQL；连接池 Druid，分页 PageHelper。

包结构以 com.shop.bookshop 为根，按职责分层：
- controller (Web 层)
- service (业务层)
- dao (持久层接口 / MyBatis mapper)
- pojo (实体)
- config/interceptor/exception/util（配置/拦截/异常/工具）。

其他资源包括：src/main/resources/templates（Thymeleaf 页面）、static（静态资源）、mappers（MyBatis XML 映射）。


#### 目录/文件逐项说明

- 项目根
  - `pom.xml`：Maven 构建与依赖。
  - `Readme.md`：运行/配置说明、初始化 DB 的步骤与默认路由。

- src/main/java/com/shop/bookshop
  - `controller/`：HTTP 请求处理器（接收请求、校验参数、调用 service、返回视图或 JSON）。
    - 典型类：`BookController`, `BookDisplayController`, `LoginRegisterController`, `OrderController`, `ShoppingCartController`, `AdminRouterController` 等。
    - 页面控制（返回视图）与接口（返回 JSON）并存。
  - `service/`：封装业务逻辑，controller 调用 service；实现类在 `service/impl`，接口在 `service` 根目录。
    - Service 会调用 DAO 操作数据库、进行事务/业务校验。
  - `dao/`：MyBatis 接口（Mapper），接口方法与 `resources/mappers/*.xml` 中的 SQL 映射对应。MyBatis 将这些接口代理为实现。
  - `pojo/`：实体类（Book, User, Order, ShoppingCart, Category, OrderItem, Admin）。这些类在 service/dao 之间传递，且被 Thymeleaf 模板渲染。
  - `config/`：框架/组件配置（如拦截器注册）。
  - `interceptor/`：如 `ClientLoginInterceptor`、`AdminInterceptor`，用于拦截特定路由，做权限/登录检查。
  - `exception/`：自定义异常与全局异常处理（统一返回错误信息或跳转）。
  - `util/`：工具/统一响应类，如响应代码枚举与统一响应结构。
  - `ResultCode`：定义统一的状态码与消息，配合 `ResultVO`（项目中存在）构成 API 响应约定。

- src/main/resources
  - `application.yml` / `application-demo.yml`：应用配置（profile、session、MyBatis mapper 路径等）。（默认 profile 为 `demo`）
  - `mappers/`：MyBatis 的 SQL 映射文件（`BookMapper.xml`、`OrderMapper.xml` 等）。Mapper XML 与 `dao` 包内的接口方法同名或按 namespace 对应。
  - `templates/`：Thymeleaf 页面（`index.html`, `details.html`, `login.html` 等）；`_fragment.html`、`_adminfragment.html` 放公共片段（页头/页尾/样式）。
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
- 修改数据库配置：编辑 `application-demo.yml` 或创建 `application.yml` 的对应 profile，调整 `spring.datasource`（示例 README 已说明）。
- 新增 SQL：在 `dao` 添加接口方法并在 `resources/mappers` 新增/修改对应 XML。
- 新增页面：在 `templates/` 添加 HTML（可使用 `_fragment` 片段），并在 `controller` 对应方法中返回视图名；静态资源放 `static/`。
- API 返回格式：修改 `util/ResultVO` 与 `util/ResultCode` 来统一前端接口规范。



### 运行与初始化
- 创建数据库：数据库名为 `bookshop`
- 在 `application-demo.yml` 中修改数据库用户名/密码
- 导入数据库脚本：`bookshop.sql`（README 提示需导入）
- 运行 `BookshopApplication` 启动应用
- 后台管理员（写死了）：账号 `admin` 密码 `123456`

```
# 打包后运行
mvn clean package -DskipTests
java -jar target\bookshop-0.0.1-SNAPSHOT.jar --spring.profiles.active=demo
```

### 访问地址与说明

- **默认主机与端口**：`http://localhost:8080/`（`server.port` 未配置时为 8080，`src/main/resources/application.yml` 中默认 profile 为 `demo`）
- **首页**：`http://localhost:8080/` 或 `http://localhost:8080/index`
- **用户登录页**：`http://localhost:8080/login`
- **用户注册页**：`http://localhost:8080/register`
- **书籍详情页**（示例 bookId=1）：`http://localhost:8080/index/books/details/1`
- **购物车 / 个人订单（需登录）**：
  - 购物车：`http://localhost:8080/{userName}/shopping_cart`（例如 `http://localhost:8080/admin/shopping_cart` 仅作格式示例）
  - 个人订单：`http://localhost:8080/{userName}/orders`

- **后台管理页面（需要管理员登录）**：
  - 后台入口 / 书籍管理：`http://localhost:8080/admin/` 或 `http://localhost:8080/admin/book_manage`
  - 后台登录页：`http://localhost:8080/admin/login`
  - 其它后台路由：`/admin/user_manage`、`/admin/category_manage`、`/admin/order_manage`、`/admin/add_book`


注意！
- 需要先启动并连接好 MySQL（数据库名 `bookshop`），否则页面会报数据错误。
- 如要修改端口运行：`java -jar target\bookshop-0.0.1-SNAPSHOT.jar --server.port=8090` 或在 `application.yml` 中设置 `server.port`。
- 登录约束：用户相关页面（购物车、订单）必须先通过 `/user/login` 登录并在 session 中存在 `user`；后台登录用 `admin` / `123456`（在 `LoginRegisterController` 中写死了）。

