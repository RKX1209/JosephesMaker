import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;

public class Main extends JFrame{
    Graphics g;
    JTabbedPane tabbedpane = new JTabbedPane();//タブ
    MainPanel panel;//絵を描く場所
    Option opt; //オプション画面
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public Main(){
	// タイトルを設定
        setTitle("Josephes Maker ver 2.0");

        // パネルを作成
        panel = new MainPanel();
	opt = new Option();
	getContentPane().add(panel);
    }

    public static void main(String[] args){
	Main frame = new Main();
	frame.setSize(WIDTH,HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
