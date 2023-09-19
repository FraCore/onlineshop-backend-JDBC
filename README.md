# onlineshop-backend-JDBC
Onlineshop Backend with JDBC

## SQL Tables

| Table  | SQL Statements                                                                                                           |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| product | CREATE TABLE products (<br>product_id INT PRIMARY KEY,<br> product_name VARCHAR(255) NOT NULL,<br> product_unit VARCHAR(255) NOT NULL,<br> product_price DECIMAL(10,2) NOT NULL<br>) |
| orders | CREATE TABLE orders (<br>order_id INT PRIMARY KEY AUTO_INCREMENT,<br>order_date DATE NOT NULL, <br>order_state CHAR(255) NOT NULL <br>) |
| positions | CREATE TABLE positions (<br> position_id INT PRIMARY KEY AUTO_INCREMENT, <br> 	position_amount INT NOT NULL, <br> product_id INT NOT NULL, <br>	order_id INT NOT NULL, <br>	FOREIGN KEY (product_id) REFERENCES products(product_id),<br>FOREIGN KEY (order_id) REFERENCES orders(order_id)<br>) |
| storages |CREATE TABLE storages (<br>	storage_id INT PRIMARY KEY,<br>	product_id INT NOT NULL, <br>  storage_stock INT NOT NULL,<br> FOREIGN KEY (product_id) REFERENCES products(product_id)<br>)|

## Rest API

### Product

| Description  | Add new Product                                                                                                           |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                 |
| Endpoints | product/add <br> http://localhost:8080/product/add                                                                        |
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4.99"}                                                                 |
| Response Code | **Product created:** _HTTP Status Code 201_ <br> **Conflict with existing Product:** _HTTP Status Code 409_ <br> **Product not valid:** _HTTP Status Code 400_ <br> **Exception:** _HTTP Status Code: 500_            |
| Response | {"id":"1"}                                                                                                    |

<br>

| Description  | Update Product                                                                                                    |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                          |
| Endpoints | product/update <br> http://localhost:8080/product/update                                                          |
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4.99"}                                                        |
| Response Code | **Product updated:** _HTTP Status Code 200_ <br> **Product not found:** _HTTP Status Code 404_ <br> **Product not valid:** _HTTP Status Code 400_ <br> **Exception:** _HTTP Status Code 500_ |
| Response | {"id":"1"}                                                                                            |

<br>

| Description  | Delete Product                                                                                                                                                                                                            |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP DELETE                                                                                                                                                                                                               |
| Endpoints | product/delete/{productId} <br> http://localhost:8080/product/delete/{productId}                                                                                                                                          |
| Payload  | None                                                                                                                                                                                                                      |
| Response Code | **Product deleted:** _HTTP Status Code 200_ <br> **Product not found:** _HTTP Status Code 404_ <br> **Product present in position:** _HTTP Status Code 400_ <br> **Product has stock:** _HTTP Status Code 400_<br>**Exception:** _HTTP Status Code 500_|
| Response | {"id":"1"}                                                                                                                                                                                                    |

<br>

| Description   | Add stock to Product                                                                                                                                                                                                     |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | product/addStock <br> http://localhost:8080/product/addStock                                                                                                                                                             |
| Payload (JSON-Format)       | {"id"="1", "amount"="100", "productId":"1"} <br> id = storage Identification                                                                                                                                                             |
| Response Code | **Stock added to product:** _HTTP Status Code 200_ <br> **Product not found:** _HTTP Status Code 404_ <br> **Missmatch productId - storageId:** _HTTPS Status Code 400_ <br>**Exception:** _HTTP Status Code 500_ |
| Response      | {"id":"1" "storageAmount":"200"}                                                                                                                                                                                                   |

<br>

| Description   | Remove stock from Product                                                                                                                                                                                                |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | product/removeStock <br> http://localhost:8080/product/removeStock                                                                                                                                                             |
| Payload (JSON-Format)       | {"id"="1", "amount"="100", "productId":"1"} <br> id = storage Identification                                                                                                                                       |
| Response Code | **Stock removed from product:** _HTTP Status Code 200_ <br> **Storage not found:** _HTTP Status Code 404_ <br> **Product not found:** _HTTP Status Code 404_ <br> **Missmatch productId - storageId:** _HTTPS Status Code 400_ <br> **Amount of Stock to low:** _HTTPS Status Code 400_ <br> **Exception:** _HTTP Status Code 500_ |
| Response      | none                                                                                                                                                                                                                     |

<br>

| Description   | Get total stock amount for Product                                                                                                                                                                                                |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP GET                                                                                                                                                                                                                |
| Endpoints     | product/getTotalStockForProduct/{productId} <br> http://localhost:8080/product/getTotalStockForProduct/{productId}                                                                                                                                                |
| Payload       | None                                                                                                                                                                         |
| Response Code | **Stock removed from product:** _HTTP Status Code 200_ <br> **Product not found:** _HTTP Status Code 404_ <br> **Exception:** _HTTP Status Code 500_ |
| Response      | {"totalStock":"200"}                                                                                                                                                                                                                     |


### Order

<br>

| Description   | Create Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | order/create <br> http://localhost:8080/order/create                                                                                                                                                |
| Payload       | None                                                                                                                                                                         |
| Response Code | **Order created:** _HTTP Status Code 201_ <br> **Exception:** _HTTP Status Code 500_ |
| Response      | {"orderId":"1"}         

<br>

| Description   | Add Product to Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | order/addProduct <br> http://localhost:8080/order/addProduct                                                                                                                                                |
| Payload (JSON-Format)       | {"amount":"200", "orderId":"1", "productId":"1"}                                                                                                                                                                         |
| Response Code | **Order created:** _HTTP Status Code 201_ <br> **Product not found:** _HTTP Status Code 404_ <br> **Order not found / closed:** _HTTP Status Code 400_ <br> **Exception:** _HTTP Status Code 500_ |
| Response      | {"positionId":"1", "totalAmount":"400"}     

<br>

| Description   | Remove Product from Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | order/removeProduct/{positionId} <br> http://localhost:8080/order/removeProduct/{positionId}                                                                                                                                                |
| Payload       | None                                                                                                                                                                 |
| Response Code | **Product removed:** _HTTP Status Code 200_ <br> **Order not found / closed:** _HTTP Status Code 400_ <br> **Position not found:** _HTTP Status Code 404_ <br> **Exception:** _HTTP Status Code 500_ |
| Response      | None    

<br>

| Description   | Delete Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP DELETE                                                                                                                                                                                                                |
| Endpoints     | order/deleteOrder/{orderId} <br> http://localhost:8080/order/deleteOrder/{orderId}                                                                                                                                                |
| Payload (JSON-Format)       | {"amount":"200", "orderId":"1", "productId":"1"}                                                                                                                                                                         |
| Response Code | **Order deleted:** _HTTP Status Code 200_ <br> **Order not found / closed:** _HTTP Status Code 400_ <br> **Exception:** _HTTP Status Code 500_ |
| Response      | None

<br>

| Description   | Submit Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP Post                                                                                                                                                                                                                |
| Endpoints     | order/submitOrder/{orderId} <br> http://localhost:8080/order/submitOrder/{orderId}                                                                                                                                               |
| Payload       | None                                                                                                                                                                       |
| Response Code | **Order submitted:** _HTTP Status Code 200_ <br> **Order not found / closed:** _HTTP Status Code 400_ <br> **Not enough Stock for Products:** _HTTP Status Code 400_<br> **Exception:** _HTTP Status Code 500_ |
| Response      | None

