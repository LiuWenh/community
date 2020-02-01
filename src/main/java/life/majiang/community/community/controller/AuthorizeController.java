package life.majiang.community.community.controller;

import life.majiang.community.community.dto.AccessTokenDTO;
import life.majiang.community.community.dto.GithubUser;
import life.majiang.community.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 总思路：
 * 1.1 登录请求发送以后，下一步调用authorize方法来获取Github社区的相关内容
 * 1.2.调用Github 的Authorize接口(在index.html文件里面，点击登录按钮，会访问authorize接口）(这里的Authorize操作包含下面的所有操作）
 * 1.2.1 Github接收到正规的Authorize验证身份成功后，会返回一个redirect_uri(是申请Autho app时候给的 URL,在index.html文件中给出了) 携带code（生成的？） ，（这里的意义和原理暂时还不知道，代码对应部分也不知道） （这里的uri管理着下面的操作，都共用一个uri)
 * 1.2.1.1 本地接受了这个code以后(how accept @RequestParam 来接收code)，就会将 accessToken携带code返回给Github （这里的accessToken是指的谁的accessToken还不知道，体现在代码的哪里也不知道）
 * 1.2.1.2 Github接受了本地的code+Github的accessTokenAPI接口之后，就会将自己的accessToken发送给本地
 * 1.2.1.3 本地会将Github 的user API (疑似在getAccessToken方法里面的url ？user access_token= 中得到体现） 连同获得的accessToken发送给Github
 * 1.2.1.4 Github会根据自己接受到的user和accessToken来返回user的信息
 * 1.2.1.5 本地历经千辛万苦，终于获取到了想要的user信息，欢喜雀跃，于是更新了自己的user信息
 */

@Controller
public class AuthorizeController {
    //Autowired 注解，就能将Component注解达到的将GithubProvider的对象放到池里的对象直接取出，而不用实例化/
    @Autowired
    private GithubProvider githubProvider;

    /**
     * 1. GithubProvider可以提供accessToken和User信息，而accessToken需要accessTokenDTO作为参数通过POST方法来获得一个截取的String
     * User需要上一步取得的Github社区的accessToken来获取到Github社区的User信息
     问题 ： 猜测在url中有 user?access_token=的字样代表什么含义？
     */

    /*?这里的GetMapping 注解后面的/callback有什么具体的规定吗？我猜是当callback的时候调用这个方法？？？？，mapping是从前端发来的网址的信息？？*/
    @GetMapping("/callback")
    public  String callback(@RequestParam(name="code")String code,
                            @RequestParam(name="state")String state) {

        AccessTokenDTO accessTokenDTO =new AccessTokenDTO();
        accessTokenDTO.setClient_id("7afd2705617325e7f1f2");
        accessTokenDTO.setClient_secret("bc8a472cdfa1d28f94c65fef723a7132d2f942a2");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8887/callback");
        accessTokenDTO.setState(state);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        /**
         * accessTokenDTO是一个Entity，能够携带5个参数，(根据这五个参数)                  《《————   Target: User Information
         * 在provider包中，根据accessTokenDTO ，得到了access_token                       ————》》   WAY   :  access_token
         * provider还根据的参数就是(accessToken)    ————》返回的是一个User类的对象    ————》》   Destination:  Successfully get GitUser
         */
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getName());
        System.out.println(user.getBio());

        //这里取到了我们需要的user信息
        return "index";
    }
}
