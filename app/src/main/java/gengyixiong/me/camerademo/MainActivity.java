package gengyixiong.me.camerademo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends ActionBarActivity {

    private ImageView imageView;
    private Button button;
    private File file;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        button    = (Button) findViewById(R.id.buttton);
        file      = new File(this.getExternalFilesDir(null),"image.jpg");       //file cannot be seen by media gallery using this path
        uri       = Uri.fromFile(file);                                         //create uri from a file

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //intent sent to camera capture
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);        //orientation of picture
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 2);                              //return to onActivityResult
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 2){
                startPhotoZoom(uri);
            }else{                      //requestcode == 3 in this case
                try{
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                    bitmap = compressImage(bitmap, 1000);

                    if (bitmap != null){
                        imageView.setImageBitmap(bitmap);
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                }catch (Exception e){
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void startPhotoZoom(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);        //image size
        intent.putExtra("outputY", 500);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat","JPEG");
        startActivityForResult(intent, 3);

    }

    public Bitmap compressImage(Bitmap image, int size){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length/1024 > size){
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(bais, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
