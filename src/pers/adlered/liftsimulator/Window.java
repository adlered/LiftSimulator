package pers.adlered.liftsimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Window extends JFrame implements ActionListener {
    boolean debugMode; //true就会让控制台刷屏
    int delay; //设定电梯上下楼速度
    int checkDelay; //设定循环检测间隔(数值越低,检测变化越快,占用系统资源越多)

    int flnow; //电梯所在楼层
    int openI = 0; //状态:展开按钮是否展开
    int max; //设定楼层最高值
    int min; //设定楼层最低值
    int guestFloor; //访客所在楼层
    int get2 = 0;
    int get3 = 0;
    boolean isGetOn = false; //检测是否上了电梯
    int count = 0; //空闲计数
    boolean autoClearlag = true;
    String get;
    String status = "等待指令"; //操作者是选择了上升还是下降
    ArrayList needUP = new ArrayList();
    JTextArea jta = new JTextArea();
    Int2String in = new Int2String(0); //初始化转换器
    //新建一个JFrame窗体
    JButton jba = new JButton("▲"); //初始化按钮
    JButton jbb = new JButton("▼");
    JLabel jl = new JLabel();
    JLabel floor = new JLabel(); //楼层按钮
    JScrollPane jsp = new JScrollPane(jta); //状态栏的滚动条
    JTextField guestF = new JTextField();
    JLabel jl5 = new JLabel("等待指令"); //上楼or下楼指示
    JTextField jtf = new JTextField(); //文本框,输入要到达的层数
    JButton okfl = new JButton("设定"); //设定要去的楼层
    JButton nowF = new JButton("设定"); //当前所在楼层
    JTextField setMaxmum = new JTextField(); //设定最大值文本框
    JButton setMaxF = new JButton("设定");
    JTextField setLowText = new JTextField();
    JButton setLowButt = new JButton("设定");
    JLabel arrf = new JLabel("请输入要到达的楼层: ");
    JTextField speedJTF = new JTextField(""); //设定电梯速度
    JButton speedJB = new JButton("设定");
    JButton openJB = new JButton("展开状态>>");
    JTextField checkJTF = new JTextField(); //设置循环速度
    JButton checkJB = new JButton("设定");
    JTextArea consoleJTA = new JTextArea();
    JScrollPane consoleJSP = new JScrollPane(consoleJTA);
    JLabel jl3 = new JLabel();
    JButton restartJB = new JButton("重启");
    JCheckBox jcb = new JCheckBox("DEBUG模式");
    JCheckBox clrJCB = new JCheckBox("自动清除缓存");

    Properties pro = new Properties();

    Random ra = new Random(); //随机数

    ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1, 300,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(3),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public void run() {
        setup();
        FrameA();
        FrameB();
        startrun();
    }

    public void setTitl(String titl) {
        setTitle(titl);
    }

    public void setup() {
        //读配置文件
        try {
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
        } catch (FileNotFoundException e) {
            pro.put("GuestDefaultFloor", "10");
            pro.put("RunDelay", "500");
            pro.put("CheckDelay", "250");
            pro.put("DebugMode", "true");
            pro.put("FloorNow", "1");
            pro.put("FloorMax", "35");
            pro.put("FloorMin", "-10");
            try {
                pro.store(new BufferedOutputStream(new FileOutputStream("config.ini")), "Save Configs File.");
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            } catch (IOException i) {
                i.printStackTrace();
            }
        } catch (IOException i) {
            i.printStackTrace();
        } finally {
            String defaultGuestFloor = pro.getProperty("GuestDefaultFloor");
            guestFloor = Integer.parseInt(defaultGuestFloor);
        }
        //读取数值
        try {
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
            String rd = pro.getProperty("RunDelay");
            delay = Integer.parseInt(rd);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        try {
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
            String cd = pro.getProperty("CheckDelay");
            checkDelay = Integer.parseInt(cd);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        try { //debugMode
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
            String dm = pro.getProperty("DebugMode");
            debugMode = Boolean.parseBoolean(dm);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        try { //flnow
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
            String fln = pro.getProperty("FloorNow");
            flnow = Integer.parseInt(fln);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        try { //max
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
            String m = pro.getProperty("FloorMax");
            max = Integer.parseInt(m);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        try { //min
            pro.load(new BufferedInputStream(new FileInputStream("config.ini")));
            String mi = pro.getProperty("FloorMin");
            min = Integer.parseInt(mi);
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
        //JFrame主窗口设定
        setSize(390, 500);
        setLocationRelativeTo(null); //窗口居中
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //点叉后关闭
        setVisible(true);
        jl.setFont(new Font("微软雅黑", Font.BOLD, 14));
        //固定窗口大小
        setResizable(false);
        //设定按钮字体
        nowF.setFont(new Font("微软雅黑", Font.BOLD, 11));
        setMaxF.setFont(new Font("微软雅黑", Font.BOLD, 11));
        setLowButt.setFont(new Font("微软雅黑", Font.BOLD, 11));
        speedJB.setFont(new Font("微软雅黑", Font.BOLD, 11));
        jta.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        openJB.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        okfl.setFont(new Font("微软雅黑", Font.BOLD, 11));
        jba.setFont(new Font("微软雅黑", Font.BOLD, 18));
        jbb.setFont(new Font("微软雅黑", Font.BOLD, 18));
        //给编辑框设定初始值
        in.setInt(delay);
        speedJTF.setText(in.returnString());
        speedJTF.setFont(new Font("微软雅黑", Font.BOLD, 14));
        in.setInt(max);
        setMaxmum.setText(in.returnString());
        setMaxmum.setFont(new Font("微软雅黑", Font.BOLD, 14));
        in.setInt(min);
        setLowText.setText(in.returnString());
        setLowText.setFont(new Font("微软雅黑", Font.BOLD, 14));
        in.setInt(guestFloor);
        guestF.setText(in.returnString());
        guestF.setFont(new Font("微软雅黑", Font.BOLD, 14));
        in.setInt(checkDelay);
        checkJTF.setText(in.returnString());
        //按钮监听
        restartJB.addActionListener(this);
        jcb.addActionListener(this);
        clrJCB.addActionListener(this);
    }

    public void FrameA() { //电梯控制面板
        setTitl("LiftSimulator");
        //文本
        jl.setBounds(10, 5, 280, 30);
        add(jl);
        //文本域
        // jta.setPreferredSize(new Dimension(200 ,150));
        add(jsp);
        jsp.setBounds(210, 20, 150, 220);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //jta.setBounds(100,80,150,200);
        jta.setEditable(false);
        jta.setText("运行日志: \n");
        AppendAndScroll(1, "线程1: \n电梯控制面板已启动.\n");
        //设定楼层
        JLabel setFlMaxMin = new JLabel("设定楼层范围: ");
        add(setFlMaxMin);
        setFlMaxMin.setFont(new Font("微软雅黑", Font.BOLD, 13));
        setFlMaxMin.setBounds(20, 135, 120, 30);
        //最高楼层
        JLabel maxF = new JLabel("最高");
        add(maxF);
        maxF.setFont(new Font("微软雅黑", Font.BOLD, 13));
        maxF.setBounds(20, 170, 40, 30);
        add(setMaxmum);
        setMaxmum.setBounds(55, 170, 40, 30);
        add(setMaxF);
        setMaxF.setBounds(105, 170, 60, 30);
        setMaxF.addActionListener(this);
        //最低楼层
        JLabel JlLowF = new JLabel("最低");
        add(JlLowF);
        JlLowF.setBounds(20, 210, 40, 30);
        JlLowF.setFont(new Font("微软雅黑", Font.BOLD, 13));
        add(setLowText);
        setLowText.setBounds(55, 210, 40, 30);
        add(setLowButt);
        setLowButt.setBounds(105, 210, 60, 30);
        setLowButt.addActionListener(this);
        //设定电梯速度
        JLabel speedJl = new JLabel("设定电梯速度(正整数): ");
        add(speedJl);
        speedJl.setBounds(20, 65, 200, 30);
        speedJl.setFont(new Font("微软雅黑", Font.BOLD, 13));
        add(speedJTF);
        speedJTF.setBounds(20, 100, 75, 30);
        add(speedJB);
        speedJB.setBounds(105, 100, 60, 30);
        speedJB.addActionListener(this);
    }

    public void FrameB() { //访客控制面板
        //创建垂直分割条
        JSeparator js = new JSeparator(JSeparator.VERTICAL);
        add(js);
        js.setBounds(380, 0, 1, 500);
        js.setForeground(Color.GRAY);
        //分割条
        JSeparator js2 = new JSeparator();
        add(js2);
        js2.setBounds(0, 250, 380, 1);
        js2.setForeground(Color.GRAY);
        JLabel jl2 = new JLabel("访客控制面板");
        add(jl2);
        jl2.setBounds(20, 260, 120, 30);
        jl2.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        //输入要到达的楼层
        Display(false);
        arrf.setBounds(20, 370, 150, 30);
        add(arrf);
        arrf.setFont(new Font("微软雅黑", Font.BOLD, 13));
        jtf.setBounds(20, 405, 75, 30);
        add(jtf);
        okfl.setBounds(105, 405, 60, 30);
        add(okfl);
        okfl.addActionListener(this);
        //按钮 电梯上
        jba.addActionListener(this);
        add(jba);
        jba.setBounds(290, 290, 60, 45);
        //按钮 电梯下
        jbb.addActionListener(this);
        add(jbb);
        jbb.setBounds(290, 350, 60, 45);
        add(jl3);
        jl3.setBounds(20, 290, 120, 30);
        jl3.setFont(new Font("微软雅黑", Font.BOLD, 13));
        jl3.setText("设定所在楼层: ");
        needUP.add(guestFloor);
        /*for (int i = 0; i < needUP.size(); i++) {
            System.out.println(needUP.get(i));
        }*/
        //设定所在楼层
        add(guestF);
        guestF.setBounds(110, 290, 55, 30);

        add(nowF);
        nowF.setBounds(170, 290, 60, 30);
        nowF.addActionListener(this);
        //已点击按钮
        JLabel jl4 = new JLabel();
        add(jl4);
        jl4.setBounds(20, 330, 120, 30);
        jl4.setFont(new Font("微软雅黑", Font.BOLD, 13));
        jl4.setText("已点击按钮: ");
        add(jl5);
        jl5.setBounds(110, 330, 120, 30);
        jl5.setFont(new Font("微软雅黑", Font.BOLD, 13));
        add(openJB);
        openJB.setBounds(250, 420, 100, 20);
        openJB.addActionListener(this);
    }

    public void startrun() {
        AppendAndScroll(1, "线程2:\n访客控制面板已启动.\n");
        //楼层显示(初始化)
        AppendAndScroll(1, "线程2:\n初始化成功.\n开始运行...\n");
        Thread cst = new CheckST();
        cst.start();
        Thread fts = new FreeTimeSelfcheck();
        fts.start();
        checkJB.addActionListener(this);
        jl.setText("电梯楼层: ");
        jl.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        floor.setBounds(70, 2, 280, 60);
        floor.setFont(new Font("微软雅黑", Font.CENTER_BASELINE, 40));
        in.setInt(flnow);
        add(floor);
        floor.setText(in.returnString().replace("-", "B"));
        in.setInt(flnow);
        int sta = 0;
        setSize(700, 500); //默认展开面板
        openJB.setText("收起状态<<");
        openI = 1;
        //开始随机数(乘客)计算
        Thread rn = new RandomNum();
        rn.start();
        while (true) { //循环检测状态(接客)
            sta++;
            if (debugMode == true) {
                AppendAndScroll(2, "Checking ButtonPushed..." + sta + "th times\n");
            }
            //System.out.println("Running...");
            if (status == "上楼") {
                AppendAndScroll(1, "电梯开始运行.\n");
                jl5.setText(status);
                int cs = 0;
                while (flnow != guestFloor) {
                    if (flnow < guestFloor) { //电梯在访客下面
                        if (cs < 4000) {
                            jta.append("↑");
                            cs++;
                        }
                        flnow++;
                        in.setInt(flnow);
                        AppendAndScroll(2, "Lift up to " + flnow + ".\n");
                        if (flnow != 0) {
                            floor.setText(in.returnString().replace("-", "B"));
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (flnow > guestFloor) { //电梯在访客上面
                        if (cs < 4000) {
                            jta.append("↓");
                            cs++;
                        }
                        flnow--;
                        in.setInt(flnow);
                        AppendAndScroll(2, "Lift down to " + flnow + ".\n");
                        if (flnow != 0) {
                            floor.setText(in.returnString().replace("-", "B"));
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                AppendAndScroll(1, "\n");
                status = "等待指令";
                jl5.setText(status);
                AppendAndScroll(1, "电梯已到达" + flnow + "层.\n");
                AppendAndScroll(1, ">>>你已经上了电梯.\n");
                Display(true);
                isGetOn = true;
            }
            if (status == "下楼") {
                jl5.setText(status);
                int cs = 0;
                while (flnow != guestFloor) {
                    if (flnow < guestFloor) { //电梯在访客下面
                        if (cs < 4000) {
                            jta.append("↑");
                            cs++;
                        }
                        flnow++;
                        in.setInt(flnow);
                        AppendAndScroll(2, "Lift up to " + flnow + ".\n");
                        if (flnow != 0) {
                            floor.setText(in.returnString().replace("-", "B"));
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (flnow > guestFloor) { //电梯在访客上面
                        if (cs < 4000) {
                            jta.append("↓");
                            cs++;
                        }
                        flnow--;
                        in.setInt(flnow);
                        AppendAndScroll(2, "Lift down to " + flnow + ".\n");
                        if (flnow != 0) {
                            floor.setText(in.returnString().replace("-", "B"));
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                AppendAndScroll(1, "\n");
                status = "等待指令";
                jl5.setText(status);
                AppendAndScroll(1, "电梯已到达" + flnow + "层.\n");
                AppendAndScroll(1, ">>>你已经上了电梯.\n");
                Display(true);
                isGetOn = true;
            }
            try {
                Thread.sleep(checkDelay);
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
        }
    }

    class CheckST extends Thread {
        public void run() { //状态检测/实时UI
            AppendAndScroll(1, "线程3:\n状态检测机制已启动.\n");
            int i = 0;
            JLabel maxJ = new JLabel("当前楼层范围:");
            add(maxJ);
            maxJ.setBounds(400, 5, 300, 30);
            maxJ.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            JLabel nowFlOn = new JLabel("当前所在楼层:");
            add(nowFlOn);
            nowFlOn.setBounds(400, 20, 300, 30);
            nowFlOn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            JLabel nowLiftSpeed = new JLabel("当前电梯速度:");
            add(nowLiftSpeed);
            nowLiftSpeed.setBounds(400, 35, 300, 30);
            nowLiftSpeed.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            JLabel check = new JLabel("设定循环检测间隔");
            JLabel check2 = new JLabel("(数值越低,检测变化越快,占用系统资源越多)");
            add(check);
            add(check2);
            check.setBounds(400, 407, 300, 30);
            check2.setBounds(400, 430, 300, 30);
            check.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            check2.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            add(checkJB);
            add(checkJTF);
            checkJB.setFont(new Font("微软雅黑", Font.PLAIN, 9));
            checkJB.setBounds(605, 410, 55, 27);
            checkJTF.setBounds(500, 410, 100, 27);
            //DEBUG模式控制钮
            add(jcb);
            jcb.setSelected(true);
            jcb.setBounds(580, 60, 150, 30);
            jcb.setFont(new Font("微软雅黑", Font.BOLD, 12));
            add(clrJCB);
            clrJCB.setSelected(true);
            clrJCB.setBounds(480, 60, 105, 30);
            clrJCB.setFont(new Font("微软雅黑", Font.BOLD, 12));
            JLabel consoleTitle = new JLabel("控制台:");
            add(consoleTitle);
            consoleTitle.setBounds(400, 60, 60, 30);
            consoleTitle.setFont(new Font("微软雅黑", Font.BOLD, 13));
            add(consoleJSP);
            consoleJSP.setBounds(400, 90, 270, 200);
            consoleJSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            consoleJTA.setBackground(Color.black);
            consoleJTA.setForeground(Color.white);
            consoleJTA.setEditable(false);
            add(restartJB);
            restartJB.setBounds(605, 375, 55, 25);
            restartJB.setFont(new Font("微软雅黑", Font.PLAIN, 9));
            while (true) {
                maxJ.setText("当前楼层范围:   " + min + "   至   " + max);
                if (isGetOn == false) {
                    nowFlOn.setText("访客所在楼层:   " + guestFloor);
                } else {
                    nowFlOn.setText("访客所在楼层:   " + flnow);
                }
                nowLiftSpeed.setText("当前电梯速度:   " + delay);
                try {
                    i++;
                    if (debugMode == true) {
                        AppendAndScroll(2, "Checking Status..." + i + "th times\n");
                    }
                    Thread.sleep(checkDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (jba == e.getSource()) {
            status = "上楼";
            AppendAndScroll(1, "接收到外部信号:上楼\n");
        }
        if (jbb == e.getSource()) {
            status = "下楼";
            AppendAndScroll(1, "接收到外部信号:下楼\n");
        }
        if (okfl == e.getSource()) {
            Display2(false);
            if (isGetOn == true) {
                get = jtf.getText();
                try {
                    get2 = Integer.parseInt(get);
                } catch (NumberFormatException n) {
                    JOptionPane.showMessageDialog(null, "请输入" + min + " 至 " + max + "内的数字", "错误", JOptionPane.ERROR_MESSAGE);
                }
                if (get2 != 0 && get2 <= max && get2 >= min) { //过滤规则
                    if (get2 < flnow) {
                        jl5.setText("下楼");
                        Thread th1 = new Down();
                        th1.start();
                    }
                    if (get2 > flnow) {
                        jl5.setText("上楼");
                        Thread th2 = new Up();
                        th2.start();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "请输入" + min + " 至 " + max + "内的数字", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                AppendAndScroll(1, "你还没有上电梯!\n");
            }
        }
        if (nowF == e.getSource()) {
            String get = guestF.getText();
            try {
                get3 = Integer.parseInt(get);
            } catch (NumberFormatException nu) {
                JOptionPane.showMessageDialog(null, "请输入" + min + " 至 " + max + "内的数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
            if (get3 != 0 && get3 <= max && get3 >= min) { //过滤规则
                guestFloor = get3;
                AppendAndScroll(1, "已将你调整到第" + get3 + "层.\n");
                AppendAndScroll(1, "<<<(由于调整层数)\n你已经下了电梯.\n");
                Display(false);
                isGetOn = false;
            } else {
                JOptionPane.showMessageDialog(null, "请输入" + min + " 至 " + max + "内的数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (setMaxF == e.getSource()) {
            String get = setMaxmum.getText();
            int out = 0;
            try {
                out = Integer.parseInt(get);
            } catch (NumberFormatException a) {
                JOptionPane.showMessageDialog(null, "请输入正确数值!", "错误", JOptionPane.ERROR_MESSAGE);
            }
            if (out != 0) {
                max = out;
                AppendAndScroll(1, "最高层数" + max + "已设定.\n");
            }
        }
        if (setLowButt == e.getSource()) {
            String get = setLowText.getText();
            int out = 0;
            try {
                out = Integer.parseInt(get);
            } catch (NumberFormatException a) {
                JOptionPane.showMessageDialog(null, "请输入正确数值!", "错误", JOptionPane.ERROR_MESSAGE);
            }
            if (out != 0) {
                min = out;
                AppendAndScroll(1, "最低层数" + min + "已设定.\n");
            }
        }
        if (speedJB == e.getSource()) {
            String get = speedJTF.getText();
            int get2 = Integer.parseInt(get);
            delay = get2;
            AppendAndScroll(1, "电梯速度已设定为" + get2 + ".\n");
        }
        if (openJB == e.getSource()) {
            if (openI == 0) { //如果没有被展开
                setSize(700, 500); //展开
                openJB.setText("收起状态<<");
                openI = 1;
            } else {
                setSize(390, 500);
                openJB.setText("展开状态>>");
                openI = 0;
            }
        }
        if (checkJB == e.getSource()) {
            int get = Integer.parseInt(checkJTF.getText());
            checkDelay = get;
            AppendAndScroll(1, "已设定循环检测间隔\n为" + checkDelay + ".\n");
        }
        if (restartJB == e.getSource()) {
            Restart();
        }
        if (jcb == e.getSource()) {
            if (jcb.isSelected()) {
                debugMode = true;
            } else {
                debugMode = false;
            }
        }
        if (clrJCB == e.getSource()) {
            if (clrJCB.isSelected()) {
                autoClearlag = true;
            } else {
                autoClearlag = false;
            }
        }
    }

    class Down extends Thread {
        public void run() {
            AppendAndScroll(1, "电梯开始下降.\n");
            int cs = 0;
            while (get2 != flnow) {
                if (cs < 4000) {
                    jta.append("↓");
                    cs++;
                }
                flnow--;
                in.setInt(flnow);
                AppendAndScroll(2, "Lift down to " + flnow + ".\n");
                if (flnow != 0) {
                    floor.setText(in.returnString().replace("-", "B"));
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException a) {
                        a.printStackTrace();
                    }
                }
            }
            AppendAndScroll(1, "\n");
            status = "等待指令";
            jl5.setText(status);
            guestFloor = get2;
            guestF.setText(get);
            AppendAndScroll(1, "电梯已到达" + flnow + "层.\n");
            AppendAndScroll(1, "<<<你已经下了电梯.\n");
            count = 0;
            Display(false);
            isGetOn = false;
            Display2(true);
        }
    }

    class Up extends Thread {
        public void run() {
            int cs = 0;
            AppendAndScroll(1, "电梯开始上升.\n");
            while (get2 != flnow) {
                if (cs < 4000) {
                    jta.append("↑");
                    cs++;
                }
                flnow++;
                in.setInt(flnow);
                AppendAndScroll(2, "Lift up to " + flnow + ".\n");
                if (flnow != 0) {
                    floor.setText(in.returnString().replace("-", "B"));
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException a) {
                        a.printStackTrace();
                    }
                }
            }
            AppendAndScroll(1, "\n");
            status = "等待指令";
            jl5.setText(status);
            guestFloor = get2;
            guestF.setText(get);
            AppendAndScroll(1, "电梯已到达" + flnow + "层.\n");
            AppendAndScroll(1, "<<<你已经下了电梯.\n");
            count = 0;
            Display(false);
            isGetOn = false;
            Display2(true);
        }
    }

    public void Display(boolean b) {
        okfl.setEnabled(b);
        jtf.setEnabled(b);
        if (b == false) {
            arrf.setForeground(Color.lightGray);
        } else {
            arrf.setForeground(Color.red);
            try {
                Thread.sleep(150);
            } catch (InterruptedException a) {
                a.printStackTrace();
            }
            arrf.setForeground(Color.black);
            try {
                Thread.sleep(150);
            } catch (InterruptedException a) {
                a.printStackTrace();
            }
            arrf.setForeground(Color.red);
            try {
                Thread.sleep(150);
            } catch (InterruptedException a) {
                a.printStackTrace();
            }
            arrf.setForeground(Color.black);
            try {
                Thread.sleep(150);
            } catch (InterruptedException a) {
                a.printStackTrace();
            }
            arrf.setForeground(Color.red);
        }
    }

    public void Display2(boolean b) {
        nowF.setEnabled(b);
        guestF.setEnabled(b);
        if (b == false) {
            jl3.setForeground(Color.lightGray);
        } else {
            jl3.setForeground(Color.black);
        }
    }

    public void Restart() { //重启软件
        runbat();
    }

    public void runbat() {
        //创建bat
        Boolean sta = false;
        File file = new File(System.getProperty("user.dir") + "\\Restart.bat");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入bat
        try {
            FileWriter fw = new FileWriter(System.getProperty("user.dir") + "\\Restart.bat");
            String s = "@echo off&&ping 127.0.0.1 -n 1 >nul&&java main&&exit";
            fw.write(s, 0, s.length());
            fw.flush();
            fw.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
        //运行bat
        String cmd = "cmd /c start /b " + System.getProperty("user.dir") + "\\Restart.bat";
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
            InputStream in = ps.getInputStream();
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //自我销毁
        System.exit(0);
    }

    class FreeTimeSelfcheck extends Thread {
        public void run() {
            while (true) { //第一循环，检测空闲时间计数
                count++;
                AppendAndScroll(2, "Free Time Counting: " + count + "...\n");
                if (count == 15 && isGetOn == false && status == "等待指令") { //空闲15秒开始轮巡自检
                    AppendAndScroll(1, "电梯开始轮巡自检.\n");
                    while (isGetOn == false && status == "等待指令") { //没人上 电梯
                        while (flnow != max && isGetOn == false && status == "等待指令") { //电梯升到最高
                            flnow++;
                            in.setInt(flnow);
                            AppendAndScroll(2, "Lift up to " + flnow + ".\n");
                            if (flnow != 0) {
                                floor.setText(in.returnString().replace("-", "B"));
                                try {
                                    Thread.sleep(delay);
                                } catch (InterruptedException a) {
                                    a.printStackTrace();
                                }
                            }
                        }
                        while (flnow != min && isGetOn == false && status == "等待指令") { //电梯升到最低
                            flnow--;
                            in.setInt(flnow);
                            AppendAndScroll(2, "Lift down to " + flnow + ".\n");
                            if (flnow != 0) {
                                floor.setText(in.returnString().replace("-", "B"));
                                try {
                                    Thread.sleep(delay);
                                } catch (InterruptedException a) {
                                    a.printStackTrace();
                                }
                            }
                        }
                    }
                }
                if (count == 15) {
                    count = 0;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Int2String {
        String ArgFloor = "";

        public Int2String(int FloorInt) {
            ArgFloor = FloorInt + "";
        }

        String returnString() {
            return ArgFloor;
        }

        void setInt(int FloorInt) {
            ArgFloor = FloorInt + "";
        }
    }

    class RandomNum extends Thread {
        public void run() {
            int n = ra.nextInt((max) + min);
            in.setInt(n);
            String n2 = in.returnString();
            //随机加负数
            if ((n2.indexOf("1")) != -1 || (n2.indexOf("3")) != -1 || (n2.indexOf("5")) != -1 || (n2.indexOf("7")) != -1) {
                n2 = "-" + n2;
            }
            if (n2.equals("0")) {
                n2 = "1";
            }
            n = Integer.parseInt(n2);
            System.out.println(n);
            needUP.add(n);
            executor.execute(new MultiGuest(n));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) { //will never happend
                e.printStackTrace();
            }
        }
    }

    class MultiGuest implements Runnable {
        int num = 999999;

        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println("working..." + num + "\n");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public MultiGuest(int num) {
            this.num = num;
        }
    }

    public void scrollAndSetCursor(JTextArea ta) { //自动滚动
        if (debugMode == true) {
            consoleJTA.append("Auto Scrolling..." + consoleJTA.getText().length() + ":" + jta.getText().length() + "words\n");
            consoleJTA.setSelectionStart(consoleJTA.getText().length()); //不这样它不换行, 我也很无奈啊...
        }
        ta.setSelectionStart(ta.getText().length());
        //如果内容太多, 清空
        if (autoClearlag == true) {
            if (consoleJTA.getText().length() > 1000000) {
                consoleJTA.setText("");
            }
        }
    }

    public void AppendAndScroll(int consoleNum, String str) {
        if (consoleNum == 1) {
            jta.append(str);
            scrollAndSetCursor(jta);
        } else if (consoleNum == 2) {
            consoleJTA.append(str);
            scrollAndSetCursor(consoleJTA);
        }
    }

}
