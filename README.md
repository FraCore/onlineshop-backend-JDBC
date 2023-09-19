# onlineshop-backend-JDBC
Onlineshop Backend with JDBC


## Rest API

### Product

| Description  | Add new Product                                                                                                           |
| ------------- |---------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP POST                                                                                                                 |
| Endpoints | product/add <br> http://localhost:8080/product/add                                                                        |
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4,99"}                                                                 |
| Response Code | Product created: HTTP Status Code 201 <br> Conflict with existing Product: HTTP Status Code 409 <br> Exception: HTTP: 500 |
| Response | Product Id: {"id":"1"}                                                                                                    |

<br>

| Description  | Update Product                                                                                                    |
| ------------- |-------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP PUT                                                                                                          |
| Endpoints | product/update <br> http://localhost:8080/product/update                                                          |
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4,99"}                                                         |
| Response Code | Product updated: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br> Exception: HTTP Status Code 500 |
| Response | Product Id: {"id":"1"}                                                                                            |

<br>

| Description  | Delete Product                                                                                                                                                                                                            |
| ------------- |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP Delete                                                                                                                                                                                                               |
| Endpoints | product/delete/{productId} <br> http://localhost:8080/product/delete/{productId}                                                                                                                                          |
| Payload  | None                                                                                                                                                                                                                      |
| Response Code | Product deleted: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br> Product present in position: HTTP Status Code 400 <br> Product has stock: HTTP Status Code 400<br>Exception: HTTP Status Code 500 |
| Response | Product Id: {"id":"1"}                                                                                                                                                                                                    |

<br>

| Description   | Add stock to Product                                                                                                                                                                                                     |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| HTTP Methode  | HTTP Post                                                                                                                                                                                                                |
| Endpoints     | product/addStock <br> http://localhost:8080/product/addStock                                                                                                                                                             |
| Payload       | {"id"="1", "amount"="100", "productId":"1"}                                                                                                                                                                              |
| Response Code | Product deleted: HTTP Status Code 200 <br> Product not found: HTTP Status Code 404 <br>Product present in position: HTTP Status Code 400 <br> Product has stock: HTTP Status Code 400<br>Exception: HTTP Status Code 500 |
| Response      | Product Id: {"id":"1"}                                                                                                                                                                                                   |




### Order

