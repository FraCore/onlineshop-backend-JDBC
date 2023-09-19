# onlineshop-backend-JDBC
Onlineshop Backend with JDBC

## Rest API

### Product

| Description  | Add new Product                                                                                                           |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                 |
| Endpoints | product/add <br> http://localhost:8080/product/add                                                                        |
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4,99"}                                                                 |
| Response Code | **Product created:** _HTTP Status Code 201_ <br> **Conflict with existing Product:** _HTTP Status Code 409_ <br> **Exception:** _HTTP Status Code: 500_            |
| Response | {"id":"1"}                                                                                                    |

<br>

| Description  | Update Product                                                                                                    |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                          |
| Endpoints | product/update <br> http://localhost:8080/product/update                                                          |
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4,99"}                                                         |
| Response Code | Product updated: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br> Exception: HTTP Status Code 500 |
| Response | {"id":"1"}                                                                                            |

<br>

| Description  | Delete Product                                                                                                                                                                                                            |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP DELETE                                                                                                                                                                                                               |
| Endpoints | product/delete/{productId} <br> http://localhost:8080/product/delete/{productId}                                                                                                                                          |
| Payload  | None                                                                                                                                                                                                                      |
| Response Code | Product deleted: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br> Product present in position: HTTP Status Code 400 <br> Product has stock: HTTP Status Code 400<br>Exception: HTTP Status Code 500|
| Response | {"id":"1"}                                                                                                                                                                                                    |

<br>

| Description   | Add stock to Product                                                                                                                                                                                                     |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP Post                                                                                                                                                                                                                |
| Endpoints     | product/addStock <br> http://localhost:8080/product/addStock                                                                                                                                                             |
| Payload (JSON-Format)       | {"id"="1", "amount"="100", "productId":"1"}                                                                                                                                                                              |
| Response Code | Stock added to product: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br> Missmatch productId - storageId: HTTPS Status Code 400 <br>Exception: HTTP Status Code 500 |
| Response      | {"id":"1" "storageAmount":"200"}                                                                                                                                                                                                   |

<br>

| Description   | Remove stock from Product                                                                                                                                                                                                |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | product/removeStock <br> http://localhost:8080/product/removeStock                                                                                                                                                             |
| Payload (JSON-Format)       | {"id"="1", "amount"="100", "productId":"1"}                                                                                                                                                                              |
| Response Code | Stock removed from product: HTTP Status Code 200 <br> Storage not found: HTTP Status Code 404 <br> Product not found: HTTP Status Code 404 <br> Missmatch productId - storageId: HTTPS Status Code 400 <br> Amount of Stock to low: HTTPS Status Code 400 <br> Exception: HTTP Status Code 500 |
| Response      | none                                                                                                                                                                                                                     |

<br>

| Description   | Get total stock amount for Product                                                                                                                                                                                                |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP GET                                                                                                                                                                                                                |
| Endpoints     | product/getTotalStockForProduct/{productId} <br> http://localhost:8080/product/getTotalStockForProduct/{productId}                                                                                                                                                |
| Payload       | None                                                                                                                                                                         |
| Response Code | Stock removed from product: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br> Exception: HTTP Status Code 500 |
| Response      | {"totalStock":"200"}                                                                                                                                                                                                                     |


### Order

<br>

| Description   | Create Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | order/create <br> http://localhost:8080/order/create                                                                                                                                                |
| Payload       | None                                                                                                                                                                         |
| Response Code | Order created: HTTP Status Code 201 <br> Exception: HTTP Status Code 500 |
| Response      | {"orderId":"1"}         

<br>

| Description   | Add Product to Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | order/addProduct <br> http://localhost:8080/order/addProduct                                                                                                                                                |
| Payload (JSON-Format)       | {"amount":"200", "orderId":"1", "productId":"1"}                                                                                                                                                                         |
| Response Code | Order created: HTTP Status Code 201 <br> Product not found: HTTP Status Code 404 <br> Order not found / closed: HTTP Status Code 400 <br> Exception: HTTP Status Code 500 |
| Response      | {"positionId":"1", "totalAmpount":"400"}     

<br>

| Description   | Remove Product from Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                                                                                                                |
| Endpoints     | order/removeProduct/{positionId} <br> http://localhost:8080/order/removeProduct/{positionId}                                                                                                                                                |
| Payload       | None                                                                                                                                                                 |
| Response Code | Product removed: HTTP Status Code 200 <br> Order not found / closed: HTTP Status Code 400 <br> Position not found: HTTP Status Code 400 <br> Exception: HTTP Status Code 500 |
| Response      | None    

<br>

| Description   | Delete Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP DELETE                                                                                                                                                                                                                |
| Endpoints     | order/deleteOrder/{orderId} <br> http://localhost:8080/order/deleteOrder/{orderId}                                                                                                                                                |
| Payload (JSON-Format)       | {"amount":"200", "orderId":"1", "productId":"1"}                                                                                                                                                                         |
| Response Code | Order deleted: HTTP Status Code 200 <br> Order not found / closed: HTTP Status Code 400 <br> Exception: HTTP Status Code 500 |
| Response      | None

<br>

| Description   | Submit Order                                                                                                                                                                                             |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP Post                                                                                                                                                                                                                |
| Endpoints     | order/submitOrder/{orderId} <br> http://localhost:8080/order/submitOrder/{orderId}                                                                                                                                               |
| Payload       | None                                                                                                                                                                       |
| Response Code | Order submitted: HTTP Status Code 200 <br> Order not found / closed: HTTP Status Code 400 <br> Not enough Stock for Products: HTTP Status Code 400<br> Exception: HTTP Status Code 500 |
| Response      | None

