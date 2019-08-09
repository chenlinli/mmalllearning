<%@ page contentType="text/html; charset=utf-8"%>
<
<html>
<body>
<h1>tomcat1</h1>
<h2>Hello World!</h2>

springmvc上传文件
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input name="upload_file" type="file"/><br>
    <input type="submit" value="upload"><br>
</form>
<hr>
富文本文件上传
<form name="form1" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input name="upload_file" type="file"/><br>
    <input type="submit" value="upload"><br>
</form>
</body>
</html>
