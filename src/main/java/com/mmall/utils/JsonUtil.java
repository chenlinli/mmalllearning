package com.mmall.utils;

import com.google.common.collect.Lists;
import com.mmall.pojo.TestPOjo;
import com.mmall.pojo.User;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class JsonUtil {

    private  static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象所有字段全部序列化
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消date默认转换timestamp形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
        //忽略空转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //设置日期转换格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //反序列化
        //忽略json中存在，java对像里不存在的错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);

    }
    public static <T> String obj2String(T obj){
        if(obj ==null){
            return null;
        }
        try {
            return obj instanceof String?(String) obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Parse Object to String error",e);
            return null;
        }

    }

    /**
     * 返沪格式化好的json字符串
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj){
        if(obj ==null){
            return null;
        }
        try {
            return obj instanceof String?(String) obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Parse Object to String error",e);
            return null;
        }

    }

    /**
     *
     *<T>:声明方法有一个泛型
     * T :返回类型为T
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>T string2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.error("Parse String to Object error",e);
            return null;
        }
    }

    /**
     * 反序列化List<T>,Map,Set
     * @param str
     * @param typeReference
     * @param <T>
     * @return
     */
    //TypeReference:jackson的
    public static <T>T string2Obj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?(T)str:objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.error("Parse String to Object error",e);
            return null;
        }
    }


    /**
     * 反序列化Map<T,E>,List ,Set
     * @param str
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T>T string2Obj(String str, Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.error("Parse String to Object error",e);
            return null;
        }
    }


    public static void main(String[] args) {
        TestPOjo testPOjo = new TestPOjo();
        testPOjo.setId(1);
        testPOjo.setName("test");
        String s = JsonUtil.obj2String(testPOjo);

        String ste ="{\"name\":\"test\",\"color\":\"blue\",\"id\":1}";
        TestPOjo t = JsonUtil.string2Obj(ste,TestPOjo.class);
        log.info("end ");


//        User user = new User();
//        user.setId(2);
//        user.setCreateTime(new Date());
//        user.setEmail("");
//        //Always
//        String userJsonPretty = JsonUtil.obj2StringPretty(user);
//        log.info(userJsonPretty);userJsonPretty



//        String userJson  = JsonUtil.obj2String(user);
//        String userJsonPretty = JsonUtil.obj2StringPretty(user);
//        log.info(userJson);
//        log.info(userJsonPretty);
//
//        User u2 = JsonUtil.string2Obj(userJson,User.class);
//        //u2!=user
//        List<User> userList = Lists.newArrayList();
//        userList.add(u2);
//        userList.add(user);
//        String userListStr = JsonUtil.obj2StringPretty(userList);
//        log.info("+++++++++++++++++++++++");
//        log.info(userListStr);
//
//        //反序列化成了List<User>
//        List<User> list = JsonUtil.string2Obj(userListStr, new TypeReference<List<User>>() {
//        });
//
//        List<User> userList1 = JsonUtil.string2Obj(userListStr,List.class,User.class);
//
//        //List反序列化里每个元素放的是LinkedHashMap
//        System.out.println("end");


    }

}
