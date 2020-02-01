package life.majiang.community.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import okhttp3.*;

import org.springframework.stereotype.Component;

import java.io.IOException;

/**总体思路：
 * 1.先通过五个参数(先化成AccessTokenDTO实体类，再化成JSON形式）为变量， API中无变量（参数必化成JSON形式）  通过步骤    获得Access_Token的String形式
 * 2.再以 Access_Token 为变量，API中直接就包含了变量Access_Token（参数不必化成JSON形式），得到了USER信息的JSON形式，进而转化为USER实体类
 * 1.实体类————》2.JSON 形式传变量值————》      系列步骤算法（核心）       ————》1.得到目标值的形式（根据算法而定是什么类型的值） ————》 2.转化得到目标值的实体类
 * */

//强大的功能，IOC????
@Component
public class GithubProvider {

    /** GET Post 方法典型*/
   public  String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
       OkHttpClient client = new OkHttpClient();

           RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
           /**这里的Post方法要求的参数，第一个是String类型的JSON数据（包含了5个变量的内容），第二个是给的默认的MediaType的Json对象
            *这里的方法我就不太懂，引入okhttp的意义就是为了获取user和accessToken,可以用官方给出的post方法，但这个方法要求的参数到底如何，并不知晓*/

           Request request = new Request.Builder()
                   .url("https://github.com/login/oauth/access_token")
                   /** 这里的URL 是获取ACCESS_TOKEN的API ，API里面无变量*/
                   .post(body)
                   /** 这里的body,包含了那5个参数，具体作用还不知道*/
                   .build();     //new Request.Builder().url().post(body).build(）
           try (Response response = client.newCall(request).execute()) {
               String string =response.body().string();
               //最终得到了一个String类型的string,然后这个string得到的并不是真正的accessToken,还需要再拆分一下
               String token= string.split("&")[0].split("=")[1];
               /**??这里视频里面的那个拆分字符串的操作属实把我给整蒙了*/
               //这样getAccessToken最终获得了Github网站对应的token，而且还是字符串类型的
               return  token;

           } catch (Exception e) {
               e.printStackTrace();
           }
           return  null;
   }

   /**     Get URL方法的典型         */
   /*     getUser方法，来获取User对象 */
    public GithubUser getUser(String accessToken) {
        /**?????   这一步说实话，也不太懂，为什么要先调用一个client，属实把爷给看蒙了 */
        OkHttpClient client = new OkHttpClient();

        /**      铁汁注意这里嗷，这里的无RequestBody构造嗷      */

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                /**URL 是获取User的API接口 ，API里就有AccessToken的变量
                 *
                 * 注意这里获得User没有post哦，获得post是在获取AccessToken的时候
                 * 因为这里的变量是在url里面，不是在body里面
                 * 在body里面的话，就是要把接收到的N个变量的参数，以JSON形式组合，获得body
                * AccessToken配上这个API，就会自动跳转到User的信息
                 * User的信息会以Json的形式放到页面上
                * */
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string =response.body().string();

            /**
             * 最终会得到一个String类型的string，但这个string并不是我想要的，我想要的是一个User类型的对象
             * 那么就调用了一个FAST JSON的方法，将string能够转换成GithubUser的类对象，自动对应，感觉还是蛮牛逼的嗷
             *至于为什么这里是JSON,还是很懵逼，不太懂*/

            GithubUser githubUser=JSON.parseObject(string,GithubUser.class);
            /*这里为何要调用FASTJSON的方法，来使String类型变成目的对象类型呢？？？*/
            return  githubUser;
        } catch (IOException e) {
        }
        return  null;
    }
}
