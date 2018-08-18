## Resources Base
> http://192.168.3.144:8080/api/v1<br>

&nbsp;

## Resources
#### Diffs and Their Locations 
* URI<br>

> /diffs<br>

* HTTP Method<br>
> POST<br>

* Request & Response Media Type<br>
> @Consumes(MediaType.MULTIPART_FORM_DATA)<br>
> @Produces({"application/json;charset=UTF-8"})<br>

* Response Parameters<br>
> text : the different string<br>
> fileNumber : which file the difference belongs to<br>
> startRow : row number of the string's first character<br>
> startCol : col number of the string's first character<br>
> endRow : row number of the string's last character<br>
> endCol : col number of the string's last character<br>

* Response Example (StatusCode 200)<br>
 
```
	
	{
	"changeDiffList": [
        {
            "endCol": 3,
            "endRow": 3,
            "fileNumber": 1,
            "startCol": 3,
            "startRow": 3,
            "text": "清"
        },
        {
            "endCol": 4,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 3,
            "startRow": 1,
            "text": "冰封"
        },
        {
            "endCol": 4,
            "endRow": 3,
            "fileNumber": 1,
            "startCol": 4,
            "startRow": 3,
            "text": "蒸"
        },
        {
            "endCol": 7,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 7,
            "startRow": 1,
            "text": "雪"
        },
        {
            "endCol": 8,
            "endRow": 3,
            "fileNumber": 1,
            "startCol": 7,
            "startRow": 3,
            "text": "烧烤"
        },
        {
            "endCol": 8,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 8,
            "startRow": 1,
            "text": "飘"
        },
        {
            "endCol": 17,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 10,
            "startRow": 1,
            "text": "一坨废柴团子！！"
        },
        {
            "endCol": 23,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 22,
            "startRow": 1,
            "text": "寒冬"
        },
        {
            "endCol": 15,
            "endRow": 3,
            "fileNumber": 1,
            "startCol": 14,
            "startRow": 3,
            "text": "酷暑"
        },
        {
            "endCol": 5,
            "endRow": 12,
            "fileNumber": 1,
            "startCol": 4,
            "startRow": 12,
            "text": "热 "
        },
        {
            "endCol": 30,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 30,
            "startRow": 1,
            "text": "冷"
        },
        {
            "endCol": 9,
            "endRow": 12,
            "fileNumber": 1,
            "startCol": 9,
            "startRow": 12,
            "text": "热"
        },
        {
            "endCol": 34,
            "endRow": 1,
            "fileNumber": 2,
            "startCol": 34,
            "startRow": 1,
            "text": "冷"
        }
    ]
	}

```


&nbsp;

## Error Code
0 &ensp; &ensp; &ensp; 请求成功

1 &ensp; &ensp; &ensp; 创建 | 更新成功

2 &ensp; &ensp; &ensp; 删除成功

999 &ensp; 未知错误

1000 &ensp; 参数错误

1001 &ensp; 资源未找到

1002 &ensp; 未经授权(token不合法)

1003 &ensp; token过期

1004 &ensp; scope权限不够

1005 &ensp; 授权失败

1006 &ensp; 客户端类型参数错误

1007 &ensp; 未知的HTTP请求异常

1008 &ensp; 超出限定时间

1009 &ensp; 文件上传错误

2001 &ensp; 在数据库中重复

6000 &ensp; 用户不存在

&nbsp;

## HTTP Status Code

200 &ensp; OK  

201 &ensp; CREATED 

202 &ensp; ACCEPTED  

301 &ensp; MOVED PERMANENTLY  

400 &ensp; BAD REQUEST 

401 &ensp; UNAUTHORIZED 

403 &ensp; FORBIDDEN 

404 &ensp; NOT FOUND  

500 &ensp; INTERNAL SERVER ERROR  


