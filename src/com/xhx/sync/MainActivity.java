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
 * @author л����
 * @date 2019��3��13��
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

    //��������״̬ 1-׼��  2-������  3-��ͣ  4-����
    private static final int STATEALREADY = 1;
    private static final int STATERUNNING = 2;
    private static final int STATEPAUSE = 3;
    private static final int STATEEND = 4;


    //ÿ����Ʊ̨�۳�Ʊ��
    private int firstSold = 0;
    private int secondSold = 0;
    private int thirdSold = 0;

    //������Ʊ״̬ 1-׼��  2-������  3-��ͣ  4-����
    private int window_state = STATEALREADY;

    //������Ʊ�����߳�
    private Thread t1;
    private Thread t2;
    private Thread t3;

    //��Ʊ����
    private int totalTicket = 100;




    //������
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //��ʼ������
                    window = new MainActivity();
                    window.frame.setVisible(true);

                    //�԰�ť�����ü���
                    window.initListener();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //�����߳�run����ʵ��
    class TicketWindowThread implements Runnable{
        @Override
        public void run() {
            

            while(totalTicket > 0){
                //�߳���ֹ
                if(Thread.interrupted())
                    return;

                //����Ʊ����ʹ��ͬ����
                synchronized (this) {
                    if(totalTicket > 0){
                        //�ж��ĸ��̡߳���Ʊ���ڡ��ڶԳ�Ʊ���в���
                        String stationName = Thread.currentThread().getName();
                        if(stationName.equals("1����Ʊ����")){

                            firstSold++;
                            System.out.println("һ�Ŵ���������Ʊ");
                            textField.setText("��Ʊ"+totalTicket+"��,һ�Ŵ���������Ʊ");
                        }
                        else if(stationName.equals("2����Ʊ����")){
                            secondSold++;
                            System.out.println("���Ŵ���������Ʊ");
                            textField.setText("��Ʊ"+totalTicket+"��,���Ŵ���������Ʊ");
                        }
                        else{
                            thirdSold++;
                            System.out.println("���Ŵ���������Ʊ");
                            textField.setText("��Ʊ"+totalTicket+"��,���Ŵ���������Ʊ");
                        }


                        try {
                            //ÿ����Ʊ����ÿ��1000ms�۳�һ��Ʊ
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


    //����Ʊ��ť���м���
    public void initListener() {
        btn_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                switch(window_state){
                    case STATEALREADY:
                        //�����ʼ��Ʊ
                        startSale();
                        break;
                    case STATERUNNING:
                        //�����ͣ��Ʊ
                        pauseSale();
                        break;
                    case STATEPAUSE:
                        //���������Ʊ
                        continueSale();
                        break;
                    case STATEEND:
                        //���ֹͣ��Ʊ
                        stopSale();
                        break;
                }
            }
        });

        //��ֹͣ��ť���м���
        btn_stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //���ֹͣ��Ʊ
                stopSale();
            }
        });
    }

    //��ʼ��Ʊ
    public void startSale(){


        totalTicket = 100;
        //��ʼ�����ڳ�Ʊ��
        firstSold = 0;
        secondSold = 0;
        thirdSold = 0;
        //��ʼ�������߳�
        TicketWindowThread twThread = new TicketWindowThread();

        t1 = new Thread(twThread,"1����Ʊ����");
        t2 = new Thread(twThread,"2����Ʊ����");
        t3 = new Thread(twThread,"3����Ʊ����");

        t1.start();
        t2.start();
        t3.start();

        //�ð�ťΪ"��ͣ"
        btn_start.setText("��ͣ��Ʊ");
        btn_stop.setEnabled(true);
        window_state = STATERUNNING;
        //������Ʊ��frame
        hideOverFrame();
    }

    //��ͣ��Ʊ
    @SuppressWarnings("deprecation")
    public void pauseSale(){
        try{
            //��ͣ��Ʊ�߳�
            t1.suspend();
            t2.suspend();
            t3.suspend();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //�ð�ťΪ"����"
            btn_start.setText("������Ʊ");
            window_state = STATEPAUSE;
        }

    }

    //���������Ʊ
    @SuppressWarnings("deprecation")
    public void continueSale(){
        //�ָ���Ʊ�߳�
        t1.resume();
        t2.resume();
        t3.resume();

        //�ð�ťΪ"��ͣ"
        btn_start.setText("��ͣ��Ʊ");
        window_state = STATERUNNING;
    }

    //���������Ʊ
    public void stopSale(){
        //����ǰ����pauseSale()��ͣ�߳��ٽ���
    	if(totalTicket > 0)
           pauseSale();

        //������Ʊ����
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();


        textField.setText("");
        btn_start.setText("���¿�ʼ��Ʊ");
        btn_stop.setEnabled(false);
        window_state = STATEALREADY;
        //��ʾ��Ʊ��frame
        showOverFrame();
    }





    public MainActivity() {
        initialize();
    }


    //����
    private void initialize() {
        frame = new JFrame();
        frame.setSize(960, 800);
        frame.setDefaultCloseOperation(3);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setForeground(Color.WHITE);
        frame.setFont(new Font("΢���ź�", Font.PLAIN, 12));
        frame.setTitle("ģ�ⴰ����Ʊ����");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        titleLabel = new JLabel();
        titleLabel.setFont(new Font("��Բ",Font.BOLD,50));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setBounds(280,50,500,100);
        titleLabel.setText("ģ�ⴰ����Ʊ����");
        frame.add(titleLabel,0);

        textField = new JLabel();
        textField.setFont(new Font("��Բ",Font.BOLD,40));
        textField.setForeground(Color.black);
        textField.setBounds(180,150,700,100);
        textField.setText("");
        frame.add(textField,0);


        btn_start = new JButton("��ʼ��Ʊ");
        btn_start.setFont(new Font("΢���ź�", Font.PLAIN, 15));
        btn_start.setBounds(250,600,140,30);
        frame.add(btn_start,0);

        btn_stop = new JButton("ֹͣ��Ʊ");
        btn_stop.setFont(new Font("΢���ź�", Font.PLAIN, 15));
        btn_stop.setBounds(550,600,140,30);
        btn_stop.setEnabled(false);
        frame.add(btn_stop,0);


        totalNumLabel = new JLabel();
        totalNumLabel.setFont(new Font("��Բ",Font.BOLD,40));
        totalNumLabel.setForeground(Color.black);
        totalNumLabel.setBounds(450,190,500,100);
        frame.add(totalNumLabel,0);

        sellFirstLabel = new JLabel();
        sellFirstLabel.setText("57");
        sellFirstLabel.setFont(new Font("��Բ",Font.BOLD,40));
        sellFirstLabel.setForeground(Color.black);
        sellFirstLabel.setBounds(632,280,500,100);
        frame.add(sellFirstLabel,0);

        sellSecondLabel = new JLabel();
        sellSecondLabel.setFont(new Font("��Բ",Font.BOLD,40));
        sellSecondLabel.setForeground(Color.black);
        sellSecondLabel.setBounds(632,355,500,100);
        frame.add(sellSecondLabel,0);

        sellThirdLabel = new JLabel();
        sellThirdLabel.setFont(new Font("��Բ",Font.BOLD,40));
        sellThirdLabel.setForeground(Color.black);
        sellThirdLabel.setBounds(632,425,500,100);
        frame.add(sellThirdLabel,0);

        hideOverFrame();
    }

    private void showOverFrame(){

        int total = firstSold + secondSold + thirdSold;
        totalNumLabel.setText("���ι���Ʊ"+String.valueOf(total)+"��");
        totalNumLabel.setVisible(true);
        sellFirstLabel.setText("һ����Ʊ"+String.valueOf(firstSold)+"��");
        sellFirstLabel.setVisible(true);
        sellSecondLabel.setText("������Ʊ"+String.valueOf(secondSold)+"��");
        sellSecondLabel.setVisible(true);
        sellThirdLabel.setText("������Ʊ"+String.valueOf(thirdSold)+"��");
        sellThirdLabel.setVisible(true);
    }

    private void hideOverFrame(){

        totalNumLabel.setVisible(false);
        sellFirstLabel.setVisible(false);
        sellSecondLabel.setVisible(false);
        sellThirdLabel.setVisible(false);
    }
}

