package com.informationUpload.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.informationUpload.R;
import com.informationUpload.activity.FindPasswordActivity;
import com.informationUpload.fragments.utils.IntentHelper;
import com.informationUpload.fragments.utils.MyFragmentManager;
import com.informationUpload.system.ConfigManager;
import com.informationUpload.system.LoginHelper;
import com.informationUpload.tool.StringTool;
import com.informationUpload.system.SystemConfig;


/**
 * @author zhjch
 * @version V1.0
 * @ClassName: LoginFragment
 * @Date 2016/1/18
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class LoginFragment extends BaseFragment {

    /**
     * 输入用户名edittext
     */
    private EditText mUserName;
    /**
     * 输入密码edittext
     */
    private EditText mPassword;
    /**
     * 登录textview
     */
    private TextView mLogin;
    /**
     * 找回密码textview
     */
    private TextView mFindPassword;
    /**
     * 快速注册按
     */
    private TextView mQuickRegister;
    /**
     * 改变密码状态
     */
    private CheckBox mChangeStatePassword;

    private View mBackView;
    private boolean mLoginOut;

    @Override
    public void onDataChange(Bundle bundle) {
        String tel = ConfigManager.getInstance().getUserTel();
        String password = ConfigManager.getInstance().getUserPassword();
        if(!TextUtils.isEmpty(tel))
            mUserName.setText(tel);
        if(!TextUtils.isEmpty(password))
            mPassword.setText(password);
        if(bundle != null){
            mLoginOut = bundle.getBoolean(SystemConfig.BUNDLE_DATA_LOGIN_OUT);
        }
        if(mLoginOut){
            mBackView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.acitivity_user_login, null, true);
        initView(view);
        return view;
    }

    private void initView(View view){
        mUserName=(EditText)view.findViewById(R.id.username_et);
        mPassword=(EditText)view.findViewById(R.id.password_et);
        mLogin=(TextView)view.findViewById(R.id.login_tv);
        mFindPassword=(TextView)view.findViewById(R.id.findpassword_tv);
        mQuickRegister=(TextView)view.findViewById(R.id.quick_register);
        mChangeStatePassword=(CheckBox)view.findViewById(R.id.change_state_password);
        mBackView = view.findViewById(R.id.fragment_back);
        addListeners();
    }

    //注册监听器
    private void addListeners() {
        mBackView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mFragmentManager.back();
            }
        });

        //登录
        mLogin.setOnClickListener(new View.OnClickListener() {
            private String name;
            private String password;
            @Override
            public void onClick(View arg0) {
                name = mUserName.getText().toString();
                password = mPassword.getText().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!StringTool.isTelNum(name)){
                    Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
//				//登录
                LoginHelper.Login(getActivity(), name, password, new LoginHelper.OnEnginCallbackListener() {
                    @Override
                    public void onSuccess() {
                        if(mIsActive) {
                            MyFragmentManager.getInstance().switchFragment(IntentHelper.getInstance().getSingleIntent(MainFragment.class, null));
                        }
                    }

                    @Override
                    public void onFailed() {

                    }
                });
                hideSoftInput(getActivity());
            }
        });
        //找回密码
        mFindPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent =new Intent(getActivity(),FindPasswordActivity.class);
                getActivity().startActivity(intent);
             
            }
        });
        //快速注册
        mQuickRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	mFragmentManager.showFragment(IntentHelper.getInstance().getSingleIntent(RegisterFragment.class,null));
//                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
                    mPassword
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    mPassword
                            .setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());
                    // inputPassword
                    // .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                    mPassword.setSelection(mPassword.getText()
                            .toString().trim().length());
                    mPassword.setTextColor(Color.parseColor("#666666"));
                } else {

                    //
                    mPassword
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    mPassword
                            .setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());
                    //
                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    mPassword.setSelection(mPassword.getText()
                            .toString().trim().length());
                    mPassword.setTextColor(Color.parseColor("#666666"));
                }
            }
        });

    }

    @Override
    public boolean onBackPressed() {
        if(mLoginOut){
            new AlertDialog.Builder(getActivity())
                    .setTitle("退出！")
                    .setMessage("您确定要退出吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("取消", null).show();
            return true;
        }
        return super.onBackPressed();
    }
}
