package com.informationUpload.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.informationUpload.R;
import com.informationUpload.system.LoginHelper;
import com.informationUpload.tool.StringTool;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: RegisterFragment
 * @Date 2016/1/18
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class RegisterFragment extends BaseFragment {
    /**
     * 电话号码输入框
     */
    private EditText mTelephoneNum;
    /**
     * 密码输入框
     */
    private EditText mRegisterPassword;

    /**
     * 注册
     */
    private TextView mRegister;
    /**
     * 返回
     */
    private RelativeLayout register_back;
    /**
     * 改变密码状态
     */
    private CheckBox mChangeStatePassword;
    /**
     * 存取用户信息的
     */
    private SharedPreferences sp;
    /**
     * 注册电话号码
     */
    private String telNum;
    /**
     * 注册密码
     */
    private String rgpassword;

    @Override
    public void onDataChange(Bundle bundle) {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.register, null, true);
        initView(view);
        //添加监听器
        addListeners();
        return view;
    }

    private void initView(View view ){
        mTelephoneNum=(EditText)view.findViewById(R.id.register_telephonenum_et);
        mRegisterPassword=(EditText)view.findViewById(R.id.register_password);
        mChangeStatePassword=(CheckBox)view.findViewById(R.id.change_state_password);
        mRegister=(TextView)view.findViewById(R.id.register_tv);
        register_back=(RelativeLayout) view.findViewById(R.id.register_back);
    }
    //添加监听器
    private void addListeners() {
        //返回
        register_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mFragmentManager.back();
            }
        });

        //注册
        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                telNum = mTelephoneNum.getText().toString();
                rgpassword = mRegisterPassword.getText().toString();

                if (TextUtils.isEmpty(telNum)) {

                    Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!StringTool.isTelNum(telNum)) {
                    Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(rgpassword)) {
                    Toast.makeText(getActivity(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //注册
                LoginHelper.register(getActivity(), telNum, rgpassword);

            }
        });
        // 密码显示隐藏
        mChangeStatePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked == true) {
                    //
                    mRegisterPassword
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                    mRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mRegisterPassword
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                    // inputPassword
                    // .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    mRegisterPassword.setSelection(mRegisterPassword.getText()
                            .toString().trim().length());
                    mRegisterPassword.setTextColor(Color.parseColor("#666666"));
                } else {
                    //
                    mRegisterPassword
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    mRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    mRegisterPassword
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    //
                    mRegisterPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    mRegisterPassword.setSelection(mRegisterPassword.getText()
                            .toString().trim().length());
                    mRegisterPassword.setTextColor(Color.parseColor("#666666"));
                }
            }
        });
    }


}