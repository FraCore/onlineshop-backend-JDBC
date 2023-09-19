# onlineshop-backend-JDBC
Onlineshop Backend with JDBC


## Rest API

### Product

| Description  | Add new Product |
| ------------- | ------------- |
| HTTP Methode  | HTTP POST  |
| Endpoints | product/add <br> http://localhost:8080/product/add|
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4,99"}  |
| Response Code | Product Created: HTTP Status Code 201 <br> Conflict with existing Product: HTTP Status Code 409 <br> Exception: HTTP: 500  |
| Response | Product Id: {"id":"1"} |

<br>

| Description  | Update Product |
| ------------- | ------------- |
| HTTP Methode  | HTTP PUT  |
| Endpoints | product/update <br> http://localhost:8080/product/update|
| Payload (JSON-Format) | {"id":"1", "name":"Tee", "unit":"500 gr", "price":"4,99"}  |
| Response Code | **OK**: HTTP Status Code 200 <br> Conflict with existing Product: HTTP Status Code 409 <br> Exception: HTTP Status Code 500  |
| Response | Product Id: {"id":"1"} |


### Order

