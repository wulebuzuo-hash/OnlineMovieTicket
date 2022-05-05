package com.android.onlinemovieticket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.fragment.Lay_bottom;
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

        showBottom();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                loginConfirm();
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
                feedbackConfirm();
                break;
            case R.id.my_callus:
                callusConfirm();
                break;
            default:
                break;
        }
    }

    private void feedbackConfirm(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.VERTICAL);
        EditText et = new EditText(this);
        et.setLayoutParams(params);
        ll.addView(et);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");
        builder.setView(ll);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(My_User.this, "感谢您的反馈", Toast.LENGTH_SHORT).show();
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

    private void callusConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统管理员");
        builder.setMessage("联系电话：18888888888");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(My_User.this, "感谢您的反馈", Toast.LENGTH_SHORT).show();
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

    private void showBottom(){
        FragmentManager manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);
        Fragment bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.my_user_frame, bottom_fragment).commit();
    }

    /**
     * 返回登录界面确认
     */
    private void loginConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回登录页面？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(My_User.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    /**
     * 加载用户头像
     */
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

    /**
     * 加载最新购买的电影票信息
     */
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
                            ticket_price.setText("￥ "+tt.getPrice());

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

    /**
     * 获取当前日期
     * @return
     */
    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    /**
     * 获取两个日期之间的天数
     * @param startDate
     * @param endDate
     * @return
     */
    private int getDateDiff(Date startDate, Date endDate) {
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return Math.abs(days);
    }

    /**
     * 获取用户从相册选择的照片
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * 更新用户头像
     * @param account
     * @param imageString
     */
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