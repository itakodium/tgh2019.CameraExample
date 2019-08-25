package events.tgh2019.androidlab.cameraexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Androidのカメラアプリとの連携サンプル
 *
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_CODE = 200;
    private final int PERM_REQ_CODE = 2000;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView cameraButton = findViewById(R.id.ivCameraButton);
        cameraButton.setOnClickListener(this);
    }

    /**
     * カメラボタンのクリックハンドラ：パーミッションのチェック～
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERM_REQ_CODE);
            return; // いったんonClickを抜けて、後続処理（再実行）は下記onRequestPermissionsResultに任せます
        }

        // 一意のURIを生成する
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String postfix = fmt.format(new Date());
        String filename = "MyPhoto" + postfix + ".jpg";

        // 画像ファイルのガワを用意し、メタデータを設定する
        ContentValues contentValue = new ContentValues();
        contentValue.put(MediaStore.Images.Media.TITLE, filename);
        contentValue.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // 画像の受け渡しができるように、URIを生成する
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue);

        // Androidのカメラアプリを起動する。
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQ_CODE);
    }

    /**
     * カメラアプリ呼び出しからの戻り。単にURIのコンテンツを貼り付ける
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == RESULT_OK) {

            ImageView imageView = findViewById(R.id.imageView0);
            imageView.setImageURI(imageUri);
        }
    }

    /**
     * onRequestPermissionsResultは、アクセス許可ダイアログを抜けた時点で呼び出される
     * ユーザの許可が得られた状態であれば、onClickハンドラと同じ処理を再実行する
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERM_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ImageView iv = findViewById(R.id.ivCameraButton);
            this.onClick(iv);
        }
    }
}
