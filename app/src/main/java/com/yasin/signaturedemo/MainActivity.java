package com.yasin.signaturedemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText mContainer;
    private SignatureView mSignView;
    private List<Bitmap> bitmaps = new ArrayList<>();
    private List<File> uploadFiles = new ArrayList<>();
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainer = (EditText) findViewById(R.id.ll_container);
        mSignView = (SignatureView) findViewById(R.id.id_sign);
        tv= (TextView) findViewById(R.id.tv);
        disableShowSoftInput(mContainer);
        mSignView.setSignatureCallBack(new SignatureView.ISignatureCallBack() {
            @Override
            public void onSignCompeleted(View view, Bitmap bitmap) {
                String fileDir=getExternalCacheDir()+"signature/";
                String path=fileDir+SystemClock.elapsedRealtime()+".png";
                File file=new File(fileDir);
                if(!file.exists()){
                    file.mkdir();
                }
                bitmaps.add(bitmap);
                try {
                    mSignView.save(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadFiles.add(new File(path));
                drawBitmaps(bitmap);
                showFiles();
            }
        });
    }

    private void showFiles() {
        tv.setText(Arrays.toString(uploadFiles.toArray()));
    }

    private void drawBitmaps(Bitmap b) {
        ImageSpan imgSpan = new ImageSpan(this, b);
        SpannableString spanString = new SpannableString("i");
        spanString.setSpan(imgSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mContainer.append(spanString);
    }

    public void delete(View view) {
        if(bitmaps.size()>0){
            bitmaps.remove(bitmaps.size()-1);
            File file=uploadFiles.get(uploadFiles.size()-1);
            uploadFiles.remove(file);
            file.delete();
            deleteText(mContainer);
            showFiles();
        }
    }

    /**
     * 获取EditText光标所在的位置
     */
    private int getEditTextCursorIndex(EditText mEditText) {
        return mEditText.getSelectionStart();
    }

    /**
     * 向EditText指定光标位置删除字符串
     */
    private void deleteText(EditText mEditText) {
        Log.e("TAG", mEditText.getText().toString());
        if (!TextUtils.isEmpty(mEditText.getText().toString())) {
            mEditText.getText().delete(getEditTextCursorIndex(mEditText) - 1, getEditTextCursorIndex(mEditText));
        }
    }

    public static void disableShowSoftInput(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                // TODO: handle exception
            }

            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}