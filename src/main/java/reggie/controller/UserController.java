package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reggie.common.R;
import reggie.entity.User;
import reggie.service.UserService;
import reggie.utils.SMSUtils;
import reggie.utils.ValidateCodeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 获取手机验证码
     * @param user
     * @return
     */
    /**
     * 诶，等等，这里还没有实现验证码限制时间的功能啊，如果验证码不限时的话，那岂不是就会被暴力破解哟
     * @param user
     * @param httpSession
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest httpServletRequest){
        log.info("获取手机验证码");

        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            //生成验证码
            //有一说一啊，教程提供的这个验证码工具类是真滴垃圾啊，只能生成4位或6位的验证码
            String code = ValidateCodeUtils.generateValidateCode4String(6);
            //模拟一下发送短信
            log.info("验证码为：{}",code);
//            SMSUtils.sendMessage("短信签名","模板编号",phone,code);

            //记得保存验证码，还得校验的
            httpServletRequest.getSession().setAttribute(phone,code);

            //嫌麻烦，也是为了好展示，验证码就默认为123456好了
//            httpSession.setAttribute(phone,"123456");
        }

        return R.success("发送验证码成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest httpServletRequest){
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        String codeSession = (String) httpServletRequest.getSession().getAttribute(phone);
        if(!codeSession.isEmpty() && codeSession.equals(code)){
            //如果用户已存在，则返回用户信息，若不存在，则先保存之，再返回用户信息
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user == null){
                //注意，此前user只是一个引用，如果为空则没有在堆里开辟新的存储空间，所以这里需要new一个
                user = new User();
                user.setPhone(phone);
                //虽然数据库对status有默认值，但是显式地写一下也是好的
                user.setStatus(1);
                userService.save(user);
            }

            //这个“user”是由过滤器中的代码决定的
            httpServletRequest.getSession().setAttribute("user",user.getId());

            return R.success(user);
        }
        return R.error("验证码错误，登陆失败");
    }
}
