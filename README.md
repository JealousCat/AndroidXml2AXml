# AndroidXml2AXml
在Android平台上，支持将一个未编译的XML文本编译为AXml（二进制Xml），并支持将它加载为一个布局视图；当然，也可以加载一个aapt编译所得的AXml为布局视图

# 优点
1.由Java实现，不借助aapt或aapt2等库，具有轻量和维护方便的特性

2.支持axml转xml（该部分功能基于[AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)）

3.支持xml转axml、axml转view，也可直接xml转view
（xml转axml或view的实现部分均有注释，可阅读帮助理解）

# 缺点
1.不支持链接上其他诸如drawable、style、layout的资源文件

2.不支持id属性，如果你写了id属性，将会被替换为tag属性，并将解析tag属性存储到一个HashMap<String, View>中

3.android命名空间外的其他命名空间可能会导致编译错误

4.目前采用tag来赋值查找子布局，由于编码格式的原因，部分tag会出现设置失效的情况。如果你使用findViewWithTag来查找子布局，可能会出现查找结果为null的情况。（这属于是一个已知BUG，暂未修复成功）
但，为了解决上述问题，使用了布局childAt来获得对应tag的子布局，其结果存储在前面所说的HashMap中，如果你要获取子布局，建议从该HashMap中获取。


# 使用说明
1.xml文件编写注意事项
如果使用的是Layout.loadXml加载布局而不是仅编译二进制xml，那么
`android:id="@+id/xxxx"`
`android:id="@id/xxxx"`
的写法会被替换为
`android:tag="xxxx"`
在视图缓存map中，应当使用xxxx名字来访问对应布局。

在编写Android Xml时，应当尽量使用tag来表示id。当然，你无需担心你在其他地方使用了setTag设置了新tag后影响map的访问。

2.XML转AXML，[参见AndroidXml2AXml](https://github.com/JealousCat/AndroidXml2AXml)
```
//public static byte[] compileXml(Context context,String str)
//public static byte[] compileXml(Context context, File file)
try{
    byte[] data = XMLBuilder.compileXml(context, new File(path));//编译xml
    FileUtils.writeByteArrayToFile(new File("xxxx.xml"),data);//写出编译结果
} catch (Exception e) {
    e.printStackTrace();
}
```

3.XML或AXML加载为View，[参见AndroidXml2AXml](https://github.com/JealousCat/AndroidXml2AXml)
```/**
* 加载一个外部或内部XML为布局
* @param context 上下文
* @param source XML文本路径 或 文本内容 或 XML 网络链接
*               <p>
*               <p>source以'/'开头，表示本地文件路径；
*               <p>以'?'开头，表示当前apk内的XML，如assets内的一个a.xml文件的路径表示为 "?assets/a.xml"，
*               <p>  如res/layout/下的a.xml文件路径表示为?res/layout/a
*               <p>  需要注意的是, res下的文件需要给它设置文件资源ID，设置为ID名和xml文件名相同
*               <p>  系统资源或者其他以加载的资源包中的xml文件，其引用方式参考Android的相关规则
*               <p>以'h'开头，表示url网络链接，将进行网络请求获得xml内容
* @param globals ID池，一个tag表示一个ID，一个ID对应一个View
* @return 加载所得布局，加载失败返回null
*/
//public static View loadXml(Context context, String source, HashMap<String, View> globals)

//public static View loadXml(Context context, File file, HashMap<String, View> globals)

//ids用于存储id或者tag所对应的View
try{
    // 其他代码...
    HashMap<String,View> ids = new HashMap<String,View>();
    View view = Layout.loadXml(context,filePath_or_xmlText,ids);
    //从res加载 Layout.loadXml(context,"?res/layout/xxx",ids); 这个xxx为文件ID名，并非文件名
    //从assets加载 Layout.loadXml(context,"?assets/a.xml",ids); assets/之后是完整的文件名
    //从网路加载 Layout.loadXml(context,"https://QQ3147359496/test.xml",ids); 传入的是文件直链
    StringBuilder sb = new StringBuilder("解析为布局：" + view + "\n");
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setView(view).setTitle("布局预览").create().show();
    sb.append("\n");
    sb.append("ID表").append(ids);
    System.out.println(sb.toString());
    System.out.println(ids.get("ID名"));
    
} catch (Exception e) {
    e.printStackTrace();
}
```

4.AXML转XML，[参见AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)

```
//从文件输入流、字节输入流、网络请求的输入流中解析AXML
try{
    byte[] data = FileUtils.readInputToByteArray(inputStream);
    String xml = Decoder.decode(context,data);
    System.out.println(xml);
    //从文件解析AXML
    String xml = Decoder.decode(context,new File(file_path));
    System.out.println(xml);
} catch (Exception e) {
    e.printStackTrace();
}
```

5.项目内提供了一个测试文件https://github.com/JealousCat/AndroidXml2AXml/test.xml，可用它测试编译或者加载效果
