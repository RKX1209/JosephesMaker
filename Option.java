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


public class Option extends JPanel implements MouseListener,MouseMotionListener,ActionListener{
    // �p�l���T�C�Y
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 800;
    // �_�̑傫��
    public static final int SIZE = 30;    
    //�l�X�̊Ԋu

    JButton OK; //OK�{�^��

    public Option() {
        // �p�l���̐����T�C�Y��ݒ�Apack()����Ƃ��ɕK�v
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
	OK = new JButton("�f�[�^�̓��v���Ƃ�");
        addMouseListener(this);
	addMouseMotionListener(this);
	OK.addActionListener(this);

	this.add(OK);
    }
    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {

    }
    public void mouseMoved(MouseEvent e) {
    }
    public void actionPerformed(ActionEvent e){
    }


}

