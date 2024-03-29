package net.n2oapp.framework.security.autoconfigure.userinfo.mapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.n2oapp.framework.security.autoconfigure.userinfo.UserInfoModel;

public class JsonToPrincipalMapper extends JsonToPrincipalAbstractMapper<UserInfoModel> {

    public UserInfoModel map(String principal) {
        UserInfoModel userInfo = new Gson().fromJson(principal, new TypeToken<UserInfoModel>() {
        }.getType());
        userInfo.authorities = collectAuthority(userInfo);
        return userInfo;
    }
}
