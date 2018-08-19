## Resources Base
> http://[hostname]:[port]/api/v1<br>

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
> text : &ensp; the different string<br>
> fileNumber : &ensp; which file the difference belongs to<br>
> startRow : &ensp; row number of the string's first character<br>
> startCol : &ensp; col number of the string's first character<br>
> endRow : &ensp; row number of the string's last character<br>
> endCol : &ensp; col number of the string's last character<br>

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
        }
    ]
}

```


&nbsp;

## Error Code

| Error Code | Description |
| :--------: | :---------: |
| 0 | 请求成功 |
| 1 | 创建 | 更新成功 |
| 2 | 删除成功 |
| 999 | 未知错误 |
| 1000 | 参数错误 |
| 1001 | 资源未找到 |
| 1002 | 未经授权(token不合法) |
| 1003 | token过期 |
| 1004 | scope权限不够 |
| 1005 | 授权失败 |
| 1006 | 客户端类型参数错误 |
| 1007 | 未知的HTTP请求异常 |
| 1008 | 超出限定时间 |
| 1009 | 文件上传错误 |
| 2001 | 在数据库中重复 |
| 6000 | 用户不存在 |

&nbsp;

## HTTP Status Code

| HTTP Status Code | Description |
| :--------------: | :---------: |
| 200 | OK |
| 201 | CREATED |
| 202 | ACCEPTED |
| 301 | MOVED PERMANENTLY |
| 400 | BAD REQUEST |
| 401 | UNAUTHORIZED |
| 403 | FORBIDDEN |
| 404 | NOT FOUND |
| 500 | INTERNAL SERVER ERROR | 
