import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.*;

import java.awt.event.*;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.*;
import java.io.File;
import java.io.*;
import java.awt.event.*;


// <Class> : ヨセフス作成のお絵描き用パネル ***********************************************************************
public class MainPanel extends JPanel implements MouseListener,MouseMotionListener,ActionListener,Runnable{
    
    public static final int WIDTH = 500; // パネルサイズ
    public static final int HEIGHT = 500;
    
    public static final int SIZE = 30;// 点の大きさ   
    public static final int Inteval = 40;//人々の間隔
    public static final int LIMIT_of_SKIP = 10000;

    public ArrayList<Person> People;//人々
    public ArrayList<Person> People_Backup;//人々のバックアップ
    public ArrayList<Integer> KillAssignment; //人々を殺す順番
    public ArrayList<Integer> Killed;//殺された人々の順番
    public ArrayList<Integer> Backup;//バックアップ
    public int now_person;//設置される人が何人目の人か
    public int LastNumber;//一番最後に置いた人の番号
    public boolean already_push_KILL;//すでにKILLボタンを押したか否か
    
    public boolean Auto_Kill;
    
    private Thread MainLoop; //メインスレッド

    
    JButton AUTO,KILL,MORE,INIT,SAVE,OPEN; //ボタン    
    JTextField TF_Skip;//スキップする人数


    // <Function>: MainPanelクラスのコンストラクタ ***********************************
    public MainPanel() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));//パネルの大きさ指定
	
        People = new ArrayList<Person>();
	People_Backup = new ArrayList<Person>();
	KillAssignment = new ArrayList<Integer>();
	Killed = new ArrayList<Integer>();
	Backup = new ArrayList<Integer>();
	
	//部品の初期化---
	KILL = new JButton("Kill");
	AUTO = new JButton("Auto");
	INIT = new JButton("RESET");
	MORE = new JButton("もう一度");
	SAVE = new JButton("保存");
	OPEN = new JButton("開く");
	TF_Skip = new JTextField("",8);
	//----------------
	

	//リスナー追加---
	addMouseListener(this);
	addMouseMotionListener(this);
	KILL.addActionListener(this);
	INIT.addActionListener(this);
	AUTO.addActionListener(this);
	OPEN.addActionListener(this);
	MORE.addActionListener(this);
	//----------------

	//パネルに部品を追加--
	this.add(KILL);
	this.add(AUTO);
	this.add(MORE);
	this.add(INIT);
	this.add(SAVE);
	this.add(OPEN);
	this.add(TF_Skip);
	//------------------
	Auto_Kill = false;
	
	MainLoop = new Thread(this);

	Init();//[*]:初期化を行う
	MainLoop.start();//[*]:メインループを走らせる
	
    }
    // </Function> *******************************************

    
    // <Function> : 初期化を行う **********************
    void Init(){
	KillAssignment.clear();//殺人リスト初期化
	People.clear();//人々を初期化
	Killed.clear();	//殺された人々を初期化
	now_person = 0;	//現在の人の番号は0
	LastNumber = 0;	//最後に設置した人も0
	already_push_KILL = false;//OKボタンはまだ押していない
	Auto_Kill = false;
	People = new ArrayList<Person>();
	Killed = new ArrayList<Integer>();
    }
    // </Function> *****************************

    
    // <Function> : スレッドの本処理 ********************************::
    public void run(){
	while(true){
	    if(Auto_Kill == true){
		if(Delete_One_Person() == false){		    
		    Auto_Kill = false;
		}
		try {
		    Thread.sleep(100);//100ms待つ
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    repaint();//再描画
	    try {
		Thread.sleep(100);//100ms待つ
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }
    // </Function> *************************************************



    // <Function> : 描画処理 *************************************::
    public void paintComponent(Graphics g) {	
        super.paintComponent(g);
	
	//存在している人々(People<Person>) 全員を描画 ------------------
        for (int i = 0; i < People.size(); i++) {
	    Person tmp = People.get(i);
            Point p = tmp.p;//その人の座標
	    String num = String.valueOf(tmp.number);//その人の番号
	    Color cl = People.get(i).color;//その人の色
	    g.setColor(cl); //描画のために人の色を設定する
            g.fillOval(p.x - SIZE / 2, p.y - SIZE / 2, SIZE, SIZE);//円を描画(x,y,直径,直径)
	    g.setColor(Color.BLACK);//描画のために人の番号の色を設定する
	    g.drawString(num,p.x,p.y);//番号を描画
        }
	//-----------------------------------------------------------
	
    }
    // </Function> *****************************************************


    
    //<Function> : マウスがクリックされた際のイベント ***********************
    public void mouseClicked(MouseEvent e) {
	mouseDragged(e);
    }
    // </Function> *****************************************



    
    // <Function> : マウスがドラッグされた際の処理 *********************
    public void mouseDragged(MouseEvent e) {		
	// クリックした座標を得る----------
        int x = e.getX();
        int y = e.getY();
	//--------------------------------

	
	if(People.size() > 0){	    
	    boolean canPut = true;//人を置けるか否か
	    boolean cross = false;//交差しているかどうか
	    for(int i = 0 ; i < People.size() ; i++){
		Person tmp = People.get(i);
		//マウスが人に重なっている場合は交差の処理をする---------------------
		if(tmp.number != LastNumber &&//一番最近に設置した人でないことが条件(設置の瞬間は自明にマウスが重なっているためこの処理はしない)
		   tmp.p.x- SIZE / 2  <= x && x <= tmp.p.x + SIZE / 2 &&
		   tmp.p.y - SIZE / 2 <= y && y <= tmp.p.y + SIZE / 2)//マウスが重なっているかどうか
		    {

			People.get(i).color = Color.GRAY;//重なっている場合は灰色にする
			KillAssignment.add(new Integer(tmp.number));//殺人リストに追加
			cross = true;//交差している
			LastNumber = tmp.number;//最後に置いた人の番号を記録
			break;
		    }
		//-------------------------------------------------------------

		else if(Dist(tmp.p.x,tmp.p.y,x,y) <= Inteval){//Inteval距離以内に他の人が配置されていたらそこには置けない
		    canPut = false;//置けない
		    break;
		}
	    }
	    if(cross == false && canPut == true){//交差ではなくて置けるなら
		People.add(new Person(now_person,x,y,Color.RED));
		LastNumber = now_person;//最後に置いた人の番号を記録
		KillAssignment.add(new Integer(now_person)); //最後に追加された人を殺すリストに追加
	    }
	    if(canPut) now_person ++;

	}
	else{
	    //People<Person>が空の場合は無条件に人を追加
	    People.add(new Person(0,x,y,Color.RED));
	    KillAssignment.add(new Integer(0));
	    LastNumber = 0;
	    now_person = 1; 
	}
        repaint();
    }
    // </Function> ****************************************************

    // <Function> : 部品によるイベント処理 ******************************
    public void actionPerformed(ActionEvent e){
	// Killボタンクリック時の処理 ----------------------------
	if(e.getSource() == KILL){	    
	    if(already_push_KILL == false){//始めてKillを押したら
		already_push_KILL = true;
	    
		int number_of_people = People.size();//人々の人数
		String text = TF_Skip.getText();
		Integer nos = new Integer(text);
		int number_of_skip = nos.intValue();
		//飛ばす人数の不正を防ぐ------------------------------
		if(!(0 < number_of_skip && number_of_skip <= LIMIT_of_SKIP)){
		    JOptionPane.showMessageDialog(null, "飛ばす人数が不正です");
		    return;
		}
		else{	    
		    int result = Alive_People(number_of_people,number_of_skip,KillAssignment);//実際に人を殺す
		    Delete_One_Person();
		}

	    }
	    //--------------------------------------------------
	    
	    //殺人リストの人々のグラッフィックを一人づつ削除していく --------------
	    else{
		Delete_One_Person();
	    }
	    //--------------------------------------------------
	    
	}
	//--------------------------------------------------
	else if(e.getSource() == AUTO){//自動で殺す
	    if(already_push_KILL == false){
		already_push_KILL = true;
		int number_of_people = People.size();//人々の人数
		String text = TF_Skip.getText();
		Integer nos = new Integer(text);
		int number_of_skip = nos.intValue();
		//飛ばす人数の不正を防ぐ------------------------------
		if(!(0 < number_of_skip && number_of_skip <= LIMIT_of_SKIP)){
		    JOptionPane.showMessageDialog(null, "飛ばす人数が不正です");
		    return;
		}
		else{	    
		    int result = Alive_People(number_of_people,number_of_skip,KillAssignment);//実際に人を殺す
		}
	    }
	    Auto_Kill = true;
	}
	else if(e.getSource() == MORE){
	    Init();
	    already_push_KILL = true;
	    for(int i = 0; i < Backup.size(); i++) Killed.add(new Integer(Backup.get(i)));
	    for(int i = 0; i < People_Backup.size(); i++) People.add(new Person(People_Backup.get(i)));
	}
	else if(e.getSource() == INIT){//初期化
	    Init();
	}else if(e.getSource() == SAVE){//ファイル保存
	    JFileChooser filechooser = new JFileChooser();	    
	    int selected = filechooser.showSaveDialog(this);
	    if (selected == JFileChooser.APPROVE_OPTION){
		File file = filechooser.getSelectedFile();
		String s_path = file.getAbsolutePath();
		try{
		    FileWriter fw = new FileWriter(s_path);
		    String str = DataToString();
		    fw.write(str);
		    fw.close();
		}catch(IOException fnfe){
		    JOptionPane.showMessageDialog(null, "処理中にエラーが発生しました");
		}
	    }
	}else if(e.getSource() == OPEN){//ファイル開く
	    JFileChooser filechooser = new JFileChooser();	    
	    int selected = filechooser.showOpenDialog(this);
	    if (selected == JFileChooser.APPROVE_OPTION){
		File file = filechooser.getSelectedFile();
		String s_path = file.getAbsolutePath();
		try{
		    FileReader fr = new FileReader(s_path);
		    String g_data = new String();
		    String k_data = new String();
		    int ch = fr.read();
		    while(ch != '*'){
			if(ch == -1){
			    JOptionPane.showMessageDialog(null, "不正なファイルです");
			    return;
			}
			g_data += (char)ch;
			ch = fr.read();
		    }
		    ch = fr.read();
		    while(ch != -1){
			k_data += (char)ch;
			ch = fr.read();
		    }
		    Init();//一旦初期化
		    StringToData(g_data,k_data);
		    fr.close();
		}catch(IOException fnfe){
		    JOptionPane.showMessageDialog(null, "処理中にエラーが発生しました");
		}
	    }
	}	    
    }
    // </Function> ******************************************************


    
    // <Function> : 一人のグラフィックデータを削除 ***************************
    public boolean Delete_One_Person(){
	if(Killed.size() > 0){
	    Delete_Person_Graphic(Killed.get(0));
	    Killed.remove(0);
	}else if(Killed.size() == 0){//残り一人になったら
	    for(int i = 0;i < People.size(); i++){
		if(People.get(i).color != Color.BLACK){
		    People.get(i).color = Color.BLUE;
		    return false;
		}
	    }
	}
	return true;
    }
    // </Function> **************************************************

    
    // <Function> : 指定された人のグラフィックスデータを削除する ********************
    public void Delete_Person_Graphic(int number_of_person){
	for(int i = 0; i < People.size(); i++){
	    if(People.get(i).number == number_of_person){//削除する人を見つけたら
		People.get(i).color = Color.BLACK;
	    }
	}	    
    }
    // </Function> **************************************************

    
    // <Function> : (x1,y1) と (x2,y2) の距離を返す *********************
    public static int Dist(int x1,int y1,int x2,int y2){
	int dx = x2 -x1;
	int dy = y2 -y1;	
	return (int)Math.sqrt(dx * dx + dy * dy);
    }
    // </Function> *********************************************


    // <Function> : People<person>のデータをStringに変換して返す **************
    public String DataToString(){
	String str = new String();
	for(int i = 0 ; i < People.size(); i++){//グラフィックを文字化
	    int n = People.get(i).number;
	    Point p = People.get(i).p;
	    Color c = People.get(i).color;
	    String tmp = String.format("%d,%d,%d,%d,",n,p.x,p.y,c.getRGB());
	    str += tmp;
	}
	str += "*"; //グラフィックと殺人リストの区切り文字
	
	for(int i = 0; i < KillAssignment.size(); i++){//殺人リストを文字化
	    Integer k = KillAssignment.get(i);
	    str += String.format("%d,",k.intValue());
	}
	return str;
    }
    // </Function> *************************************************


    // <Function> : String文字列をPeopler<person>,KillAssignment<Integer>に変換する ****************
    public void StringToData(String g,String k){	
	String[] gs = g.split(",");//グラフィックデータを,区切りで追加
	String[] ks = k.split(",");//殺人リストデータを,区切りで追加
	for(int i = 0; i < gs.length; i+=4){
	    Integer n = new Integer(gs[i]);
	    Integer x = new Integer(gs[i + 1]);
	    Integer y = new Integer(gs[i + 2]);
	    Integer rgb = new Integer(gs[i + 3]);
	    People.add(new Person(n.intValue(),x.intValue(),y.intValue(),new Color(rgb.intValue())));
	}
	for(int i = 0; i < ks.length; i++){
	    Integer kill = new Integer(ks[i]);
	    KillAssignment.add(kill);
	}	
    }
    // </Function> *****************************************************


    // <Function> : 実際に人々を殺し、誰が生き残るかを返す *****************************
    public int Alive_People(int Number_Of_People,int Number_Of_Skip,ArrayList<Integer> Move){	
	boolean alive[] = new boolean[100000000];//0: Death  1: Alive
	
	Arrays.fill(alive,true);
	int Number_Of_Alive_People = Number_Of_People;  
	int Kill = 0;
  
	while(Number_Of_Alive_People > 1){
	    for(int d = 0; d < Number_Of_Skip + 1; ){
		Kill++;
		Kill = Kill % Move.size();
		if(alive[Move.get(Kill).intValue()] == true) d++;
	    }
	    alive[Move.get(Kill).intValue()] = false;//殺害	    
	    Killed.add(new Integer(Move.get(Kill).intValue()));
	    Number_Of_Alive_People--;
	}
	Backup.clear();
	People_Backup.clear();
	for(int i = 0; i < Killed.size(); i++) Backup.add(new Integer(Killed.get(i)));
	for(int i = 0; i < People.size(); i++) People_Backup.add(new Person(People.get(i)));

	for(int d = 0; d < Move.size(); d++)
	    if(alive[Move.get(d)] == true) return Move.get(d).intValue();
	return -1;
    }
    // </Function> ***********************************************************************

    
    public void mouseMoved(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }



}
// </class> *************************************************************************



// <class> : 人一人についてのクラス ******************************:
class Person{
    public int number;//人の番号
    public Point p;//人の座標
    public Color color;//人の色

    // <Function> : Personクラスのコンストラクタ *****************
    public Person(){
	number = 0;
	p = new Point(0,0);
	color = Color.RED;
    }
    public Person(int n,int x,int y,Color c){
	number = n;
	p = new Point(x,y);
	color = c;
    }
    public Person(Person t){
	number = t.number;
	p = new Point(t.p.x,t.p.y);
	color = t.color;
    }
    // </Function> *****************************::::

    
}
// </class> *********************************************:
