# diff2texts
Compare two texts, get differences and their locations in each text.

### INFO
* 这是一个基于`Jersey RESTful`框架的Maven项目，实现了获取两个文本文件的不同字符，以及这些字符在相应文本中位置的功能。<br>
  需要注意的是，无法获取空行，但可以获取其他空白字符。<br>

* 从diff_match_patch（ https://github.com/google/diff-match-patch ） 中修改了部分方法，作为字符串比较方法。

### API
* 参考doc/API_Code.md

### Examples

> text1

```
安-225运输机（俄文/乌克兰文：АH-225，英文：An-225 Mriya，中文：米莉亚/梦幻；北约代号：Cossack，中文：哥萨克，通称：安东诺夫安-225），是苏联安东诺夫设计（Антонов/Antonov）研制的超大型军用运输机。
安-225运输机是为了运输暴风雪号航天飞机而研制，机身顶部最大载重200吨，机身长度84米，翼展88.4米，是目前世界上最重、尺寸最大的飞机。
```

> text2

```


安-225运输机（俄文/乌克兰文：АH-225，英文：An-225 Mriya，中文：米莉亚/梦幻；北约代号：Cossack，中文：哥萨克，通称：安东诺夫安-225），是苏联安东诺夫设计（Антонов/Antonov）研制的超大型军用运输机。
安-225运输机是为运输暴风雪号航天飞机而研制，最大起飞重量640吨，货舱最大载重250吨，机身顶部最大载重200吨，机身长度84米，翼展88.4米，是    目前世界上最重、尺寸最大的飞机
```

> diffs

```
{
    "changeDiffList": [
        {
            "endCol": 11,
            "endRow": 2,
            "fileNumber": 1,
            "startCol": 11,
            "startRow": 2,
            "text": "了"
        },
        {
            "endCol": 46,
            "endRow": 4,
            "fileNumber": 2,
            "startCol": 25,
            "startRow": 4,
            "text": "最大起飞重量640吨，货舱最大载重250吨，"
        },
        {
            "endCol": 80,
            "endRow": 4,
            "fileNumber": 2,
            "startCol": 77,
            "startRow": 4,
            "text": "    "
        },
        {
            "endCol": 71,
            "endRow": 2,
            "fileNumber": 1,
            "startCol": 71,
            "startRow": 2,
            "text": "。"
        }
    ]
}
```


