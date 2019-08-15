package raihana.msd.rgl;

import android.app.DatePickerDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;

import raihana.msd.rgl.adapter.ViewPagerAdapter;
import raihana.msd.rgl.fragment.AFragment;
import raihana.msd.rgl.fragment.BFragment;
import raihana.msd.rgl.fragment.BaseFragment;
import raihana.msd.rgl.fragment.CFragment;
import raihana.msd.rgl.fragment.DFragment;
import raihana.msd.rgl.fragment.EFragment;
import raihana.msd.rgl.fragment.FFragment;
import raihana.msd.rgl.fragment.GFragment;
import raihana.msd.rgl.fragment.HFragment;
import raihana.msd.rgl.utils.DateUtils;
import raihana.msd.rgl.utils.SharedPreference;

public class MainActivity extends AppCompatActivity {
    private SharedPreference sharedPreference;
    private TextView tvStoreCode, tvStoreName;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private EditText etDate1, etDate2;
    private String date_out1, date_out2;
    private DateUtils dateUtils;
    private int viewPagerPosition;
    private AFragment aFragment = new AFragment();
    private BFragment bFragment = new BFragment();
    private CFragment cFragment = new CFragment();
    private DFragment dFragment = new DFragment();
    private EFragment eFragment = new EFragment();
    private FFragment fFragment = new FFragment();
    private GFragment gFragment = new GFragment();
    private HFragment hFragment = new HFragment();


    public String getDate_out1() {
        return date_out1;
    }
    public String getDate_out2() {
        return date_out2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initTabs();
        initData();
        initListener();
    }

    private void initListener() {
        etDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String string_date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        try {
                            date_out1 = dateUtils.reFormatDate(string_date, DateUtils.SDF_FORMAT1, DateUtils.SDF_FORMAT2);
                            etDate1.setText(date_out1);
                            Fragment fragment = viewPagerAdapter.getItem(viewPagerPosition);
                            if (fragment instanceof BaseFragment)initFragmentData((BaseFragment) fragment);
                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int i, float v, int i1) {

                                }

                                @Override
                                public void onPageSelected(int i) {
                                    /*switch (i) {
                                        case 0:
                                            afFragment.sync();
                                            break;
                                        case 1:
                                            bFragment.sync();
                                            break;
                                        default:
                                            break;
                                    }*/
                                }

                                @Override
                                public void onPageScrollStateChanged(int i) {

                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, dateUtils.getCurrentYear(), dateUtils.getCurrentMonth(), dateUtils.getCurrentDays());
                dialog.show();
            }
        });

        etDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String string_date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        try {
                            date_out2 = dateUtils.reFormatDate(string_date, DateUtils.SDF_FORMAT1, DateUtils.SDF_FORMAT2);
                            etDate2.setText(date_out2);
                            Fragment fragment = viewPagerAdapter.getItem(viewPagerPosition);
                            if (fragment instanceof BaseFragment)
                                initFragmentData((BaseFragment) fragment);
                            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int i, float v, int i1) {

                                }

                                @Override
                                public void onPageSelected(int i) {
                                    switch (i) {
                                        case 0:
                                            break;
                                        case 1:
                                            bFragment.sync();
                                            break;
                                        default:
                                            break;
                                    }
                                }

                                @Override
                                public void onPageScrollStateChanged(int i) {

                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, dateUtils.getCurrentYear(), dateUtils.getCurrentMonth(), dateUtils.getCurrentDays());
                dialog.show();
            }
        });
    }

    private void initComponents() {
        sharedPreference = new SharedPreference(this);
        dateUtils = new DateUtils();
        tvStoreCode = findViewById(R.id.tv_store_code);
        etDate1 = findViewById(R.id.et_date_1);
        etDate2 = findViewById(R.id.et_date_2);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void initData() {
        tvStoreCode.setText(sharedPreference.getObjectData("username", String.class));

        String today = dateUtils.formatCurrentDate(DateUtils.SDF_FORMAT2);
        int firstDay = dateUtils.getCurrentDays() - (dateUtils.getCurrentDays() - 1);
        String firstDayString = firstDay + "/" + (dateUtils.getCurrentMonth() + 1) + "/" + dateUtils.getCurrentYear();
        try {
            etDate1.setText(dateUtils.reFormatDate(firstDayString, DateUtils.SDF_FORMAT1, DateUtils.SDF_FORMAT2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        etDate2.setText(dateUtils.formatCurrentDate(DateUtils.SDF_FORMAT2));
        date_out1 = etDate1.getText().toString();
        date_out2 = etDate2.getText().toString();

        sharedPreference.storeData("today", today);
    }

    private void initTabs() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(aFragment, "DAILY SALES");
        viewPagerAdapter.addFragment(bFragment, "DAILY STORE");
        viewPagerAdapter.addFragment(cFragment, "BEST ARTICLE");
        viewPagerAdapter.addFragment(dFragment, "BEST STORE");
        viewPagerAdapter.addFragment(eFragment, "OPEN ORDER");
        viewPagerAdapter.addFragment(fFragment, "ON PROGRESS");
        viewPagerAdapter.addFragment(gFragment, "ON DELIVERY");
        viewPagerAdapter.addFragment(hFragment, "COMPLETED");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initFragmentData(BaseFragment fragment) {
        fragment.sync();
    }
}
