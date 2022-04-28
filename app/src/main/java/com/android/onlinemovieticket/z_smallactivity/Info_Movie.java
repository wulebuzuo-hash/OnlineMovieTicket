package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.repository.MovieRepository;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Info_Movie extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private Button navButton;
    private TextView titleName;

    private EditText nameEdit;
    private EditText nameEngEdit;

    private TextView screenEdit;
    private Button screenButton;

    private TextView typeEdit;
    private Button typeButton;
    private List<String> typeList = new ArrayList<>(3);
    private EditText storyEdit;
    private EditText longEdit;
    private EditText actorEdit;
    private EditText directorEdit;

    private TextView showdateEdit;
    private Button showButton;
    private TextView downdateEdit;
    private Button downButton;
    private String date;

    private Button readImg;
    private Button add;
    private ProgressBar progressBar;

    private ImageView img;
    Uri uri;
    private String imageString;

    private String account;
    private String type;
    private int mid;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_movie);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("编辑电影");

        nameEdit = (EditText) findViewById(R.id.info_movie_name);
        nameEngEdit = (EditText) findViewById(R.id.info_movie_englishname);

        screenEdit = (TextView) findViewById(R.id.info_movie_screen);
        screenButton = (Button) findViewById(R.id.info_movie_screen_button);
        screenButton.setOnClickListener(this);

        typeEdit = (TextView) findViewById(R.id.info_movie_type);
        typeButton = (Button) findViewById(R.id.info_movie_type_button);
        typeButton.setOnClickListener(this);
        storyEdit = (EditText) findViewById(R.id.info_movie_story);
        longEdit = (EditText) findViewById(R.id.info_movie_long);
        actorEdit = (EditText) findViewById(R.id.info_movie_actor);
        directorEdit = (EditText) findViewById(R.id.info_movie_director);

        showdateEdit = (TextView) findViewById(R.id.info_movie_showdate);
        showButton = (Button) findViewById(R.id.info_movie_showdateButton);
        showButton.setOnClickListener(this);

        downdateEdit = (TextView) findViewById(R.id.info_movie_downdate);
        downButton = (Button) findViewById(R.id.info_movie_downdateButton);
        downButton.setOnClickListener(this);

        readImg = (Button) findViewById(R.id.info_movie_readimg);
        readImg.setOnClickListener(this);

        img = (ImageView) findViewById(R.id.info_movie_choose_img);
        progressBar = (ProgressBar) findViewById(R.id.info_movie_progressbar);
        mid = getIntent().getIntExtra("mid", 0);
        if (mid != 0) {
            progressBar.setVisibility(View.VISIBLE);
            loadMovie();
        }


        add = (Button) findViewById(R.id.info_movie_submit);
        add.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_movie_progressbar);
        progressBar.setVisibility(View.GONE);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                Intent intent = new Intent(Info_Movie.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
                break;
            case R.id.info_movie_readimg:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");//图片
                startActivityForResult(galleryIntent, 0);
                break;
            case R.id.info_movie_screen_button:
                chooseScreen();
                break;
            case R.id.info_movie_type_button:
                chooseType();
                break;
            case R.id.info_movie_submit:
                progressBar.setVisibility(View.VISIBLE);
                if (movie == null) {
                    if (nameEdit.getText().toString().equals("") ||
                            nameEngEdit.getText().toString().equals("") ||
                            storyEdit.getText().toString().equals("") ||
                            longEdit.getText().toString().equals("") ||
                            actorEdit.getText().toString().equals("") ||
                            directorEdit.getText().toString().equals("") ||
                            showdateEdit.getText().toString().equals("") ||
                            downdateEdit.getText().toString().equals("") ||
                            screenEdit.getText().toString().equals("") ||
                            typeEdit.getText().toString().equals("")) {
                        Toast.makeText(Info_Movie.this, "请填写完整信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        addMovie();
                    }
                } else {
                    updateMovie();
                }

                break;
            case R.id.info_movie_showdateButton:
                //获取实例，包含当前年月日
                Calendar calendar = Calendar.getInstance();
                date = "show";
                DatePickerDialog dialog = new DatePickerDialog(this, this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MARCH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.info_movie_downdateButton:
                //获取实例，包含当前年月日
                Calendar calendar1 = Calendar.getInstance();
                date = "down";
                DatePickerDialog dialog1 = new DatePickerDialog(this, this,
                        calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MARCH),
                        calendar1.get(Calendar.DAY_OF_MONTH));
                dialog1.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String desc = String.format("%d-%d-%d", i, i1 + 1, i2);
        if (date.equals("show")) {
            showdateEdit.setText(desc);
        } else if (date.equals("down")) {
            downdateEdit.setText(desc);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        if (requestCode == 0 && resultCode == -1) {
            uri = data.getData();
            img.setImageURI(uri);
            Log.i("tt", uri.getPath());
            Log.i("tt", uri.getEncodedPath());

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void chooseScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] items = {"2D", "3D"};
        builder.setTitle("选择屏幕");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                screenEdit.setText(items[which]);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void chooseType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最多选择三个");
        String[] types = {"动作", "喜剧", "爱情", "科幻", "恐怖", "剧情", "战争", "犯罪",
                "惊悚", "悬疑", "文艺", "动画", "纪录片", "其他"};

        builder.setMultiChoiceItems(types, null,
                new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    typeList.add(types[which]);
                } else {
                    typeList.remove(types[which]);
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (typeList.size()) {
                    case 1:
                        typeEdit.setText(typeList.get(0));
                        break;
                    case 2:
                        typeEdit.setText(typeList.get(0) + "/" + typeList.get(1));
                        break;
                    case 3:
                        typeEdit.setText(typeList.get(0) + "/" +
                                typeList.get(1) + "/" + typeList.get(2));
                        break;
                    default:
                        typeList.clear();
                        Toast.makeText(getApplicationContext(),
                                "最少选择一个，最多选择三个", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void loadMovie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieRepository movieRepository = new MovieRepository();
                movie = movieRepository.getMovieByMid(mid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (movie != null) {
                            nameEdit.setText(movie.getMname());
                            nameEngEdit.setText(movie.getMname_eng());
                            screenEdit.setText(movie.getMscreen());
                            typeEdit.setText(movie.getMtype());
                            storyEdit.setText(movie.getMstory());
                            showdateEdit.setText(movie.getShowdate().toString());
                            downdateEdit.setText(movie.getDowndate().toString());
                            longEdit.setText(movie.getMlong() + "");
                            actorEdit.setText(movie.getMactor());
                            directorEdit.setText(movie.getMdir());

                            String[] typeMovie = movie.getMtype().split("/");
                            for (int i = 0; i < typeMovie.length; i++) {
                                typeList.add(typeMovie[i]);
                            }

                            byte[] image = Base64.decode(movie.getImgString(), Base64.DEFAULT);
                            Bitmap decodedImg = BitmapFactory.decodeByteArray(image, 0,
                                    image.length);
                            img.setImageBitmap(decodedImg);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void addMovie() {
        new Thread() {
            @Override
            public void run() {
                String name = nameEdit.getText().toString();
                String nameEng = nameEngEdit.getText().toString();
                String screen = screenEdit.getText().toString();
                String type = typeEdit.getText().toString();
                String story = storyEdit.getText().toString();
                String mlong = longEdit.getText().toString();
                String actor = actorEdit.getText().toString();
                String director = directorEdit.getText().toString();
                String showdate = showdateEdit.getText().toString();
                String downdate = downdateEdit.getText().toString();
                try {
                    Date show = new Date(new SimpleDateFormat("yyyy-MM-dd").
                            parse(showdate).getTime());
                    Date down = new Date(new SimpleDateFormat("yyyy-MM-dd").
                            parse(downdate).getTime());
                    Movie movie = new Movie(name, nameEng, screen, type, story,
                            Integer.valueOf(mlong), imageString, show, down, actor, director);
                    MovieRepository movieRepository = new MovieRepository();
                    int msg = 0;
                    Movie mm = movieRepository.findMovie(movie.getMname());
                    if (mm != null) {
                        msg = 1;
                    } else {
                        boolean flag = movieRepository.addMovie(movie);
                        if (flag) {
                            msg = 2;
                        }
                    }

                    hand.sendEmptyMessage(msg);


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateMovie() {
        new Thread() {
            @Override
            public void run() {
                int msg = 2;
                String name = nameEdit.getText().toString();
                String nameEng = nameEngEdit.getText().toString();
                String screen = screenEdit.getText().toString();
                String type = typeEdit.getText().toString();
                String story = storyEdit.getText().toString();
                String mlong = longEdit.getText().toString();
                String actor = actorEdit.getText().toString();
                String director = directorEdit.getText().toString();
                String showdate = showdateEdit.getText().toString();
                String downdate = downdateEdit.getText().toString();
                try {
                    Date show = new Date(new SimpleDateFormat("yyyy-MM-dd").
                            parse(showdate).getTime());
                    Date down = new Date(new SimpleDateFormat("yyyy-MM-dd").
                            parse(downdate).getTime());
                    Movie mm = new Movie(movie.getMid(), name, nameEng, screen, type, story,
                            Integer.valueOf(mlong), imageString, show, down, actor, director);
                    MovieRepository movieRepository = new MovieRepository();
                    boolean flag = movieRepository.updateMovie(mm);
                    if (flag) {
                        msg = 3;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(Info_Movie.this,
                        "添加失败，请检查网络或联系系统管理员！！", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                Toast.makeText(Info_Movie.this, "该电影名已经存在，请换一个电影名",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 2) {
                Toast.makeText(Info_Movie.this, "添加成功",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent1 = new Intent(Info_Movie.this, MovieActivity.class);
                intent1.putExtra("account", account);
                intent1.putExtra("type", type);
                startActivity(intent1);
                finish();
            } else if (msg.what == 3) {
                Toast.makeText(Info_Movie.this, "修改成功",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent1 = new Intent(Info_Movie.this, MovieActivity.class);
                intent1.putExtra("account", account);
                intent1.putExtra("type", type);
                startActivity(intent1);
                finish();
            }
        }
    };
}