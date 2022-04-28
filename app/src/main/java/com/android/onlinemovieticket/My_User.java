package com.android.onlinemovieticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;
import com.android.onlinemovieticket.repository.UserRepository;
import com.android.onlinemovieticket.z_smallactivity.List_Admin;
import com.android.onlinemovieticket.z_smallactivity.List_Ticket;
import com.android.onlinemovieticket.z_smallactivity.List_Uh;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class My_User extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private ImageView my_image;
    private RadioButton my_imageButton;
    private Uri uri;
    private String imageString;

    private TextView accountEdit;
    private RadioButton updateMessage;

    private ProgressBar progressBar;

    private LinearLayout ticket_lay;
    private TextView ticket_no;
    private ImageView ticket_img;
    private TextView ticket_price;
    private TextView ticket_name;
    private TextView ticked_code;
    private TextView ticket_seat;
    private TextView ticket_time;
    private TextView ticket_cinema;
    private String cname;
    private TextView ticket_hall;
    private String hname;

    private List<Ticket> ticketList = new ArrayList<>();
    private List<Session> sessionList = new ArrayList<>();
    private Session ss;
    private Ticket tt;
    private Movie movie;

    private RadioButton goTicket;

    private RadioButton feedback;
    private RadioButton callus;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private String account;
    private String type;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_user);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("我的");

        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);

        my_image = (ImageView) findViewById(R.id.my_image);
        my_imageButton = (RadioButton) findViewById(R.id.my_imageButton);
        my_image.setOnClickListener(this);
        my_imageButton.setOnClickListener(this);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        accountEdit = (TextView) findViewById(R.id.my_account);
        accountEdit.setText(account);
        progressBar = (ProgressBar) findViewById(R.id.my_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        loadUserImage();

        updateMessage = (RadioButton) findViewById(R.id.my_updateMessage);
        updateMessage.setOnClickListener(this);
        Drawable drawable_news2 = getResources().getDrawable(R.drawable.pc_set);
        drawable_news2.setBounds(0, 0, 100, 100);
        updateMessage.setCompoundDrawables(null,null,drawable_news2, null);

        goTicket = (RadioButton) findViewById(R.id.my_goTicket);
        goTicket.setOnClickListener(this);
        Drawable drawable_news = getResources().getDrawable(R.drawable.pc_more);
        drawable_news.setBounds(0, 0, 100, 100);
        goTicket.setCompoundDrawables(drawable_news,null,null, null);

        ticket_lay = (LinearLayout) findViewById(R.id.my_user_ticket_lay);
        ticket_no = (TextView) findViewById(R.id.my_user_ticket_no);
        ticket_img = (ImageView) findViewById(R.id.item_ticket_image);
        ticket_price = (TextView) findViewById(R.id.item_ticket_price);
        ticket_name = (TextView) findViewById(R.id.item_ticket_name);
        ticked_code = (TextView) findViewById(R.id.item_ticket_code);
        ticket_seat = (TextView) findViewById(R.id.item_ticket_seat);
        ticket_time = (TextView) findViewById(R.id.item_ticket_time);
        ticket_cinema = (TextView) findViewById(R.id.item_ticket_cinema);
        ticket_hall = (TextView) findViewById(R.id.item_ticket_hall);

        initTicket();

        feedback = (RadioButton) findViewById(R.id.my_feedback);
        feedback.setOnClickListener(this);
        callus = (RadioButton) findViewById(R.id.my_callus);
        callus.setOnClickListener(this);

        bottom_1 = (RadioButton) findViewById(R.id.bottom_choose_movie);
        bottom_1.setOnClickListener(this);
        bottom_2 = (RadioButton) findViewById(R.id.bottom_choose_cinema);
        bottom_2.setOnClickListener(this);
        bottom_3 = (RadioButton) findViewById(R.id.bottom_choose_my);
        bottom_3.setOnClickListener(this);

        setBounds(R.drawable.pc_movie,bottom_1);
        setBounds(R.drawable.pc_cinema,bottom_2);
        setBounds(R.drawable.my,bottom_3);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                Intent intent = new Intent(My_User.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.my_image:
            case R.id.my_imageButton:
                progressBar.setVisibility(View.VISIBLE);
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");//图片
                startActivityForResult(galleryIntent, 0);
                break;
            case R.id.my_updateMessage:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.my_goTicket:
                Intent intent2 = new Intent(My_User.this, List_Ticket.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);
                intent2.putExtra("ticketList", (Serializable) ticketList);
                intent2.putExtra("sessionList", (Serializable) sessionList);
                startActivity(intent2);
                break;
            case R.id.my_feedback:
                break;
            case R.id.my_callus:
                break;
            case R.id.bottom_choose_movie:
                Intent intent3 = new Intent(My_User.this, MovieActivity.class);
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent4 = new Intent(My_User.this, CinemaActivity.class);
                intent4.putExtra("account", account);
                intent4.putExtra("type", type);
                startActivity(intent4);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent5 = null;
                if (type.equals("用户")) {
                    intent5 = new Intent(My_User.this, My_User.class);
                } else if (type.equals("管理员")) {
                    intent5 = new Intent(My_User.this, List_Uh.class);
                } else if (type.equals("BOSS")) {
                    intent5 = new Intent(My_User.this, List_Admin.class);
                }
                intent5.putExtra("account", account);
                intent5.putExtra("type", type);
                startActivity(intent5);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param drawableId  drawableLeft  drawableTop drawableBottom 所用的选择器 通过R.drawable.xx 获得
     * @param radioButton  需要限定图片大小的radioButton
     */
    private void setBounds(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形  (这里的长和宽写死了 自己可以可以修改成 形参传入)
        drawable_news.setBounds(0, 0, 120, 120);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(null,drawable_news,null, null);
    }

    private void loadUserImage(){
        new Thread(){
            @Override
            public void run() {
                UserRepository userRepository = new UserRepository();
                String imageString = userRepository.getImgByAccount(account);
                byte[] image = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedImg = BitmapFactory.decodeByteArray(image, 0, image.length);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        my_image.setImageBitmap(decodedImg);
                    }
                });
            }
        }.start();
    }

    private void initTicket() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                TicketRepository ticketRepository = new TicketRepository();
                ticketList = ticketRepository.getTicketByAccount(account);
                if (ticketList.size() == 0) {
                    msg = 1;
                } else {
                    SessionRepository sessionRepository = new SessionRepository();
                    for(Ticket ticket : ticketList) {
                        sessionList.add(sessionRepository.getSessionBySid(ticket.getSid()));
                    }

                    Date currentDate = getNowDate();
                    int days = getDateDiff(currentDate,sessionList.get(0).getShowDate());
                    for(Session session : sessionList) {
                        if(getDateDiff(session.getShowDate(),currentDate) <= days) {
                            days = getDateDiff(session.getShowDate(),currentDate);
                            ss = session;
                        }
                    }

                    for(Ticket ticket : ticketList) {
                        if(ticket.getSid() == ss.getSid()) {
                            tt = ticket;
                            break;
                        }
                    }

                    CinemaRepository cinemaRepository = new CinemaRepository();
                    cname = cinemaRepository.getCnameByCid(ss.getCid());
                    HallRepository hallRepository = new HallRepository();
                    hname = hallRepository.getHnameByHid(ss.getHid());
                    MovieRepository movieRepository = new MovieRepository();
                    movie = movieRepository.getMovieByMid(ss.getMid());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
                            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes,
                                    0, imageBytes.length);
                            ticket_img.setImageBitmap(decodedImage);
                            ticket_name.setText(movie.getMname());
                            ticked_code.setText(tt.getTicket_code());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                            String date = dateFormat.format(ss.getShowDate());
                            String time = dateFormat2.format(ss.getShowTime());
                            ticket_time.setText(date + " " + time);
                            ticket_cinema.setText(cname);
                            ticket_hall.setText(hname);
                            ticket_price.setText("￥ "+String.valueOf(ss.getPrice()));

                            String seat = tt.getSeat();
                            seat = seat.replace(",","排");
                            seat = seat.replace(";","座 ");
                            ticket_seat.setText(seat);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    //获取现在时间
    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    //计算日期差
    public int getDateDiff(Date startDate, Date endDate) {
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return Math.abs(days);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        if (requestCode == 0 && resultCode == -1) {
            uri = data.getData();
            my_image.setImageURI(uri);
            Log.i("tt", uri.getPath());
            Log.i("tt", uri.getEncodedPath());

            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                updateImage(account, imageString);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void updateImage(String account, String imageString) {
        new Thread() {
            @Override
            public void run() {
                int msg = -1;
                if (type.equals("用户")) {
                    UserRepository userRepository = new UserRepository();
                    boolean flag = userRepository.updateImage(account, imageString);
                    if (flag) {
                        msg = 2;
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == -1) {
                Toast.makeText(getApplicationContext(), "编辑头像失败，请检查网络",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                ticket_lay.setVisibility(View.GONE);
                ticket_no.setVisibility(View.VISIBLE);
            } else if (msg.what == 2) {
                Toast.makeText(getApplicationContext(), "编辑成功",
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}