package com.android.onlinemovieticket.z_smallactivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class Info_Movie extends AppCompatActivity implements View.OnClickListener{

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

    private TextView actorEdit;
    private List<EditText> actorEditList = new ArrayList<>(5);
    private Button actorButton;

    private TextView directorEdit;
    private List<EditText> directorEditList = new ArrayList<>(3);
    private Button directorButton;

    private TextView showdateEdit;
    private Button showButton;
    private TextView downdateEdit;
    private Button downButton;

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

        actorEdit = (TextView) findViewById(R.id.info_movie_actor);
        actorButton = (Button) findViewById(R.id.info_movie_actor_button);
        actorButton.setOnClickListener(this);
        directorEdit = (TextView) findViewById(R.id.info_movie_director);
        directorButton = (Button) findViewById(R.id.info_movie_director_button);
        directorButton.setOnClickListener(this);

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
                backConfirm();
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
                        progressBar.setVisibility(View.VISIBLE);
                        addMovie();
                    }
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    updateMovie();
                }

                break;
            case R.id.info_movie_showdateButton:
                //获取实例，包含当前年月日
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                String desc = String.format("%d-%d-%d", i, i1 + 1, i2);
                                showdateEdit.setText(desc);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MARCH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMinDate(calendar.getTimeInMillis());
                dialog.show();
                break;
            case R.id.info_movie_downdateButton:
                //获取实例，包含当前年月日

                Calendar calendar1 = Calendar.getInstance();
                DatePickerDialog dialog1 = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                String desc = String.format("%d-%d-%d", i, i1 + 1, i2);
                                downdateEdit.setText(desc);
                            }
                        },
                        calendar1.get(Calendar.YEAR),
                        calendar1.get(Calendar.MARCH),
                        calendar1.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker1 = dialog1.getDatePicker();
                datePicker1.setMinDate(System.currentTimeMillis());
                dialog1.show();
                break;
            case R.id.info_movie_actor_button:
                chooseActor();
                break;
            case R.id.info_movie_director_button:
                chooseDirector();
                break;
            default:
                break;
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

    private void backConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回首页？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Info_Movie.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void chooseActor() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                WRAP_CONTENT, WRAP_CONTENT);

        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ll1 = new LinearLayout(this);
        ll1.setLayoutParams(params);
        ll1.setOrientation(LinearLayout.VERTICAL);

        final EditText editText = new EditText(Info_Movie.this);
        editText.setLayoutParams(params);
        editText.setHint("请输入演员名字");
        ll1.addView(editText);
        actorEditList.add(editText);

        LinearLayout ll2 = new LinearLayout(this);
        ll2.setLayoutParams(params);
        ll2.setGravity(Gravity.CENTER);
        ll2.setOrientation(LinearLayout.HORIZONTAL);

        Button addActor = new Button(this);
        addActor.setLayoutParams(params2);
        addActor.setText("添加演员");
        addActor.setTextSize(20);
        addActor.setTextColor(Color.RED);
        addActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actorEditList.size() < 5) {
                    final EditText editText = new EditText(Info_Movie.this);
                    editText.setLayoutParams(params);
                    editText.setHint("请输入演员名字");
                    ll1.addView(editText);
                    actorEditList.add(editText);
                } else {
                    Toast.makeText(Info_Movie.this, "最多只能添加5个演员",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll2.addView(addActor);

        Button deleteActor = new Button(this);
        deleteActor.setText("删除演员");
        deleteActor.setTextSize(20);
        deleteActor.setTextColor(Color.GREEN);
        deleteActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actorEditList.size() > 1) {
                    actorEditList.remove(actorEditList.size() - 1);
                    ll1.removeView(actorEditList.get(actorEditList.size() - 1));
                } else {
                    Toast.makeText(Info_Movie.this, "至少要有一个演员",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll2.addView(deleteActor);

        ll.addView(ll1);
        ll.addView(ll2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");
        builder.setView(ll);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                initActor();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    private void initActor() {
        String actor = "";
        for (int i = 0; i < actorEditList.size(); i++) {
            if (!actorEditList.get(i).getText().toString().equals("")) {
                actor += actorEditList.get(i).getText().toString() + "/";
            }
        }
        if (!actor.equals("")) {
            actorEdit.setText(actor.substring(0, actor.length() - 1));
        } else {
            actorEdit.setText("");
        }
        progressBar.setVisibility(View.GONE);
    }

    private void chooseDirector() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                WRAP_CONTENT, WRAP_CONTENT);

        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ll1 = new LinearLayout(this);
        ll1.setLayoutParams(params);
        ll1.setOrientation(LinearLayout.VERTICAL);

        final EditText editText = new EditText(Info_Movie.this);
        editText.setLayoutParams(params);
        editText.setHint("请输入导演名字");
        ll1.addView(editText);
        directorEditList.add(editText);

        LinearLayout ll2 = new LinearLayout(this);
        ll2.setGravity(Gravity.CENTER);
        ll2.setLayoutParams(params);
        ll2.setOrientation(LinearLayout.HORIZONTAL);

        Button adddirector = new Button(this);
        adddirector.setLayoutParams(params2);
        adddirector.setText("继续添加");
        adddirector.setTextSize(20);
        adddirector.setTextColor(Color.RED);
        adddirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (directorEditList.size() < 3) {
                    final EditText editText = new EditText(Info_Movie.this);
                    editText.setLayoutParams(params);
                    editText.setHint("请输入导演名字");
                    ll1.addView(editText);
                    directorEditList.add(editText);
                } else {
                    Toast.makeText(Info_Movie.this, "最多只能添加3个导演",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll2.addView(adddirector);

        Button deletedirector = new Button(this);
        deletedirector.setText("删除");
        deletedirector.setTextSize(20);
        deletedirector.setTextColor(Color.GREEN);
        deletedirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (directorEditList.size() > 1) {
                    directorEditList.remove(directorEditList.size() - 1);
                    ll1.removeView(directorEditList.get(directorEditList.size() - 1));
                } else {
                    Toast.makeText(Info_Movie.this, "至少要有一个导演",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll2.addView(deletedirector);

        ll.addView(ll1);
        ll.addView(ll2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");
        builder.setView(ll);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                initDirector();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void initDirector() {
        String director = "";
        for (int i = 0; i < directorEditList.size(); i++) {
            if (!directorEditList.get(i).getText().toString().equals("")) {
                director += directorEditList.get(i).getText().toString() + "/";
            }
        }
        if (!director.equals("")) {
            directorEdit.setText(director.substring(0, director.length() - 1));
        } else {
            directorEdit.setText("");
        }
        progressBar.setVisibility(View.GONE);
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