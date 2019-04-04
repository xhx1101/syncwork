package com.xhx.sync;

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;


import java.awt.Color;

/**
 * @author 谢洪鑫
 * @date 2019年3月13日
 */
public class MainActivity{
    private static MainActivity window;
    private JFrame frame;
    private JButton btn_start;
    private JButton btn_stop;
    private JLabel totalNumLabel;
    private JLabel sellFirstLabel;
    private JLabel sellSecondLabel;
    private JLabel sellThirdLabel;
    private JLabel titleLabel;
    private JLabel textField;

    //运行三种状态 1-准备  2-进行中  3-暂停  4-结束
    private static final int STATEALREADY = 1;
    private static final int STATERUNNING = 2;
    private static final int STATEPAUSE = 3;
    private static final int STATEEND = 4;


    //每个售票台售出票数
    private int firstSold = 0;
    private int secondSold = 0;
    private int thirdSold = 0;

    //窗口售票状态 1-准备  2-进行中  3-暂停  4-结束
    private int window_state = STATEALREADY;

    //三个售票窗口线程
    private Thread t1;
    private Thread t2;
    private Thread t3;

    //车票总数
    private int totalTicket = 100;




    //主函数
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //初始化界面
                    window = new MainActivity();
                    window.frame.setVisible(true);

                    //对按钮等设置监听
                    window.initListener();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //窗口线程run方法实现
    class TicketWindowThread implements Runnable{
        @Override
        public void run() {
            

            while(totalTicket > 0){
                //线程终止
                if(Thread.interrupted())
                    return;

                //对售票过程使用同步锁
                synchronized (this) {
                    if(totalTicket > 0){
                        //判断哪个线程【售票窗口】在对车票进行操作
                        String stationName = Thread.currentThread().getName();
                        if(stationName.equals("1号售票窗口")){

                            firstSold++;
                            System.out.println("一号窗口正在售票");
                            textField.setText("余票"+totalTicket+"张,一号窗口正在售票");
                        }
                        else if(stationName.equals("2号售票窗口")){
                            secondSold++;
                            System.out.println("二号窗口正在售票");
                            textField.setText("余票"+totalTicket+"张,二号窗口正在售票");
                        }
                        else{
                            thirdSold++;
                            System.out.println("三号窗口正在售票");
                            textField.setText("余票"+totalTicket+"张,三号窗口正在售票");
                        }


                        try {
                            //每个售票窗口每隔1000ms售出一张票
                            Thread.sleep(1000);
                            totalTicket--;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            if(totalTicket <= 0)
                stopSale();

        }
    }


    //对售票按钮进行监听
    public void initListener() {
        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                switch(window_state){
                    case STATEALREADY:
                        //点击开始售票
                        startSale();
                        break;
                    case STATERUNNING:
                        //点击暂停售票
                        pauseSale();
                        break;
                    case STATEPAUSE:
                        //点击继续售票
                        continueSale();
                        break;
                    case STATEEND:
                        //点击停止售票
                        stopSale();
                        break;
                }
            }
        });

        //对停止按钮进行监听
        btn_stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //点击停止售票
                stopSale();
            }
        });
    }

    //开始售票
    public void startSale(){


        totalTicket = 100;
        //初始化窗口出票数
        firstSold = 0;
        secondSold = 0;
        thirdSold = 0;
        //初始化窗口线程
        TicketWindowThread twThread = new TicketWindowThread();

        t1 = new Thread(twThread,"1号售票窗口");
        t2 = new Thread(twThread,"2号售票窗口");
        t3 = new Thread(twThread,"3号售票窗口");

        t1.start();
        t2.start();
        t3.start();

        //置按钮为"暂停"
        btn_start.setText("暂停售票");
        btn_stop.setEnabled(true);
        window_state = STATERUNNING;
        //隐藏售票数frame
        hideOverFrame();
    }

    //暂停售票
    @SuppressWarnings("deprecation")
    public void pauseSale(){
        try{
            //暂停售票线程
            t1.suspend();
            t2.suspend();
            t3.suspend();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //置按钮为"继续"
            btn_start.setText("继续售票");
            window_state = STATEPAUSE;
        }

    }

    //点击继续售票
    @SuppressWarnings("deprecation")
    public void continueSale(){
        //恢复售票线程
        t1.resume();
        t2.resume();
        t3.resume();

        //置按钮为"暂停"
        btn_start.setText("暂停售票");
        window_state = STATERUNNING;
    }

    //点击结束售票
    public void stopSale(){
        //结束前调用pauseSale()暂停线程再结束
    	if(totalTicket > 0)
           pauseSale();

        //结束售票进程
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();


        textField.setText("");
        btn_start.setText("重新开始售票");
        btn_stop.setEnabled(false);
        window_state = STATEALREADY;
        //显示售票数frame
        showOverFrame();
    }





    public MainActivity() {
        initialize();
    }


    //布局
    private void initialize() {
        frame = new JFrame();
        frame.setSize(960, 800);
        frame.setDefaultCloseOperation(3);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setForeground(Color.WHITE);
        frame.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        frame.setTitle("模拟窗口售票界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("幼圆",Font.BOLD,50));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setBounds(280,50,500,100);
        titleLabel.setText("模拟窗口售票过程");
        frame.add(titleLabel,0);

        textField = new JLabel();
        textField.setFont(new Font("幼圆",Font.BOLD,40));
        textField.setForeground(Color.black);
        textField.setBounds(180,150,700,100);
        textField.setText("");
        frame.add(textField,0);


        btn_start = new JButton("开始售票");
        btn_start.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        btn_start.setBounds(250,600,140,30);
        frame.add(btn_start,0);

        btn_stop = new JButton("停止售票");
        btn_stop.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        btn_stop.setBounds(550,600,140,30);
        btn_stop.setEnabled(false);
        frame.add(btn_stop,0);


        totalNumLabel = new JLabel();
        totalNumLabel.setFont(new Font("幼圆",Font.BOLD,40));
        totalNumLabel.setForeground(Color.black);
        totalNumLabel.setBounds(450,190,500,100);
        frame.add(totalNumLabel,0);

        sellFirstLabel = new JLabel();
        sellFirstLabel.setText("57");
        sellFirstLabel.setFont(new Font("幼圆",Font.BOLD,40));
        sellFirstLabel.setForeground(Color.black);
        sellFirstLabel.setBounds(632,280,500,100);
        frame.add(sellFirstLabel,0);

        sellSecondLabel = new JLabel();
        sellSecondLabel.setFont(new Font("幼圆",Font.BOLD,40));
        sellSecondLabel.setForeground(Color.black);
        sellSecondLabel.setBounds(632,355,500,100);
        frame.add(sellSecondLabel,0);

        sellThirdLabel = new JLabel();
        sellThirdLabel.setFont(new Font("幼圆",Font.BOLD,40));
        sellThirdLabel.setForeground(Color.black);
        sellThirdLabel.setBounds(632,425,500,100);
        frame.add(sellThirdLabel,0);

        hideOverFrame();
    }

    private void showOverFrame(){

        int total = firstSold + secondSold + thirdSold;
        totalNumLabel.setText("本次共售票"+String.valueOf(total)+"张");
        totalNumLabel.setVisible(true);
        sellFirstLabel.setText("一号售票"+String.valueOf(firstSold)+"张");
        sellFirstLabel.setVisible(true);
        sellSecondLabel.setText("二号售票"+String.valueOf(secondSold)+"张");
        sellSecondLabel.setVisible(true);
        sellThirdLabel.setText("三号售票"+String.valueOf(thirdSold)+"张");
        sellThirdLabel.setVisible(true);
    }

    private void hideOverFrame(){

        totalNumLabel.setVisible(false);
        sellFirstLabel.setVisible(false);
        sellSecondLabel.setVisible(false);
        sellThirdLabel.setVisible(false);
    }
}

