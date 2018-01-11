package com.alguojian.rxjavademo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.alguojian.rxjavademo.R;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.socks.library.KLog;

import io.reactivex.Observable;

import static com.alguojian.rxjavademo.base.MyApplication.TTAG;

public class Main4Activity extends AppCompatActivity {


    protected EditText name;
    protected EditText age;
    protected EditText job;
    protected Button push;
    private Observable<CharSequence> nameObser;
    private Observable<CharSequence> ageObser;
    private Observable<CharSequence> jobObser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main4);
        initView();

        /**
         * 为每个EditText设置被观察者，用于发送监听事件
         * *1. 此处采用了RxBinding：RxTextView.textChanges(name) = 对对控件数据变更进行监听（功能类似TextWatcher），
         * 2. 传入EditText控件，点击任1个EditText撰写时，都会发送数据事件 = Function3（）的返回值（下面会详细说明）
         * 3. 采用skip(1)原因：跳过 一开始EditText无任何输入时的空值
         */


        init();



    }

    /**
     * 通过combineLatest（）合并事件 & 联合判断
     * <p>
     * 当两个Observables中的任何一个发送了数据后，
     * 将先发送了数据的Observables 的最新（最后）一个数据 与
     * 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据
     */
    private void init() {

        nameObser = RxTextView.textChanges(name).skip(1);
        ageObser = RxTextView.textChanges(age).skip(1);
        jobObser = RxTextView.textChanges(job).skip(1);

        Observable.combineLatest(nameObser, ageObser, jobObser,
                (charSequence, charSequence2, charSequence3) -> {
                    boolean nameIsNOtEmpty = !TextUtils.isEmpty(name.getText());

                   // boolean nameIs = !TextUtils.isEmpty(name.getText()) && name.getText().toString().length() <= 10;
                    boolean ageIsNotEmpty = !TextUtils.isEmpty(age.getText());
                    boolean jobIsNotEmpty = !TextUtils.isEmpty(job.getText());

                    return nameIsNOtEmpty && ageIsNotEmpty && jobIsNotEmpty;
                }
        ).subscribe(aBoolean -> {

            KLog.d(TTAG, "点击结果是：" + aBoolean);
            push.setEnabled(aBoolean);
        });
    }

    private void initView() {
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        job = findViewById(R.id.job);
        push = findViewById(R.id.push);
    }
}
