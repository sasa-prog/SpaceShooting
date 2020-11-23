package io.github.sasagu.shooting;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

public class Shooting {
	public static ShootingFrame shootingFrame;
    public static boolean loop;

	/**
	 * @param args
	 */
	public static void main(String[] args){
		// TODO Auto-generated method stub
		shootingFrame = new ShootingFrame();
        loop = true;

        Graphics gra = shootingFrame.panel.image.getGraphics();

        //FPS
        long startTime;
        long fpsTime = 0;
        int fps = 30;
        int FPS = 0;
        int FPSCount = 0;

        EnumShootingScreen screen = EnumShootingScreen.START;

        //GAME
        int playerX = 0, playerY = 0;
        int bulletInterval = 0;
        int score = 0;
        int level = 0;
        long levelTimer = 0;
        int flushcount = 0;
        ArrayList<Bullet> bullets_player = new ArrayList<>();
        ArrayList<Bullet> bullets_enemy = new ArrayList<>();
        ArrayList<Enemy> enemies = new ArrayList<>();
        Random random = new Random();


        while(loop) {

            if((System.currentTimeMillis() - fpsTime) >= 1000) {
                fpsTime = System.currentTimeMillis();
                FPS = FPSCount;
                FPSCount = 0;
            }
            FPSCount++;
            startTime = System.currentTimeMillis();

            gra.setColor(Color.BLACK);
            gra.fillRect(0, 0, 500, 500);

            switch (screen) {
                case START:
                    gra.setColor(Color.YELLOW);
                    Font font = new Font("SansSerif", Font.PLAIN, 50);
                    gra.setFont(font);
                    FontMetrics metrics = gra.getFontMetrics(font);
                    gra.drawString("Shooting", 250 - (metrics.stringWidth("Shooting") / 2), 100);
                    font = new Font("SansSerif", Font.PLAIN, 20);
                    gra.setFont(font);
                    metrics = gra.getFontMetrics(font);
                    gra.drawString("Press SPACE to Start", 250 - (metrics.stringWidth("Press SPACE to Start") / 2), 160);
                    java.io.BufferedReader br = null;
                    flushcount = 0;
                    gra.drawOval(400, 360, 50, 50);
                    gra.setColor(Color.BLUE);
                    gra.drawOval(50,160,60,60);

				try {
					br = new java.io.BufferedReader(new java.io.FileReader("score.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					gra.drawString("HighScore:0", (metrics.stringWidth("HighScore:0"))/2, 180);
					new File("score.txt");
				}

				if(br!=null) {
					try {
						gra.drawString("HighScore:"+br.readLine(),250 - (metrics.stringWidth("HighScore:???") /2),180);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally {
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
                    if(Keyboard.isKeyPressed(KeyEvent.VK_SPACE)) {
                        screen = EnumShootingScreen.GAME;
                        bullets_player = new ArrayList<>();
                        bullets_enemy = new ArrayList<>();
                        enemies = new ArrayList<>();
                        playerX = 235;
                        playerY = 430;
                        score = 0;
                        level = 0;
                    }
                    break;
                case GAME:
                    if(System.currentTimeMillis() - levelTimer > 10 * 1000) {
                        levelTimer = System.currentTimeMillis();
                        level++;
                    }

                    gra.setColor(Color.BLUE);
                    gra.fillRect(playerX + 10, playerY, 10, 10);
                    gra.fillRect(playerX, playerY + 10, 30, 10);

                    for (int i = 0; i < bullets_player.size(); i++) {
                        Bullet bullet = bullets_player.get(i);
                        gra.fillRect(bullet.x, bullet.y, 5, 5);
                        bullet.y -= 10;
                        if (bullet.y < 0) {
                            bullets_player.remove(i);
                            i--;
                        }

                        for (int l = 0; l < enemies.size(); l++) {
                            Enemy enemy = enemies.get(l);
                            if(bullet.x>=enemy.x&&bullet.x<=enemy.x+30&&
                            bullet.y>=enemy.y&&bullet.y<=enemy.y+20) {
                                enemies.remove(l);
                                score += 10;
                            }
                        }
                    }

                    gra.setColor(Color.RED);
                    for (int i = 0; i < enemies.size(); i++) {
                        Enemy enemy = enemies.get(i);
                        gra.fillRect(enemy.x, enemy.y, 30, 10);
                        gra.fillRect(enemy.x + 10, enemy.y + 10, 10, 10);
                        enemy.y += 3;
                        if(enemy.y > 500) {
                            enemies.remove(i);
                            i--;
                        }
                        if(random.nextInt(level<50?80 - level:30)==1) bullets_enemy.add(new Bullet(enemy.x + 12, enemy.y));
                        if((enemy.x>=playerX&&enemy.x<=playerX+30&&
                                enemy.y>=playerY&&enemy.y<=playerY+20)||
                            (enemy.x+30>=playerX&&enemy.x+30<=playerX+30&&
                                    enemy.y+20>=playerY&&enemy.y+20<=playerY+20)) {
                            screen = EnumShootingScreen.GAME_OVER;
                            score += (level - 1) * 100;
                        }
                    }
                    if(random.nextInt(level<10?30 - level:10)==1) enemies.add(new Enemy(random.nextInt(470), 0));

                    for (int i = 0; i < bullets_enemy.size(); i++) {
                        Bullet bullet = bullets_enemy.get(i);
                        gra.fillRect(bullet.x, bullet.y, 5, 5);
                        bullet.y += 10;
                        if (bullet.y > 500) {
                            bullets_enemy.remove(i);
                            i--;
                        }
                        if(bullet.x>=playerX&&bullet.x<=playerX+30&&
                        bullet.y>=playerY&&bullet.y<=playerY+20) {
                            screen = EnumShootingScreen.GAME_OVER;
                            score += (level - 1) * 100;
                        }
                    }

                    if(Keyboard.isKeyPressed(KeyEvent.VK_LEFT)&&playerX>0) playerX-=8;
                    if(Keyboard.isKeyPressed(KeyEvent.VK_RIGHT)&&playerX<470) playerX+=8;
                    if(Keyboard.isKeyPressed(KeyEvent.VK_UP)&&playerY>30) playerY-=8;
                    if(Keyboard.isKeyPressed(KeyEvent.VK_DOWN)&&playerY<450) playerY+=8;

                    if(Keyboard.isKeyPressed(KeyEvent.VK_SPACE)&&bulletInterval==0) {
                        bullets_player.add(new Bullet(playerX + 12, playerY));
                        bulletInterval = 8;
                    }
                    if(bulletInterval>0) bulletInterval--;
                    if(Keyboard.isKeyPressed(KeyEvent.VK_F)) {
                    	if(enemies.size()>0&&flushcount<5) {
                    		enemies.remove(0);
                    		flushcount++;
                    	}
                    }

                    gra.setColor(Color.YELLOW);
                    font = new Font("SansSerif", Font.PLAIN, 20);
                    metrics = gra.getFontMetrics(font);
                    gra.setFont(font);
                    gra.drawString("SCORE:" + score, 470 - metrics.stringWidth("SCORE:" + score), 430);
                    gra.drawString("LEVEL:" + level, 470 - metrics.stringWidth("LEVEL:" + level), 450);

                    break;
                case GAME_OVER:
                    gra.setColor(Color.YELLOW);
                    font = new Font("SansSerif", Font.PLAIN, 50);
                    gra.setFont(font);
                    metrics = gra.getFontMetrics(font);
                    gra.drawString("Game Over", 250 - (metrics.stringWidth("Game Over") / 2), 100);
                    font = new Font("SansSerif", Font.PLAIN, 20);
                    gra.setFont(font);
                    metrics = gra.getFontMetrics(font);
                    gra.setColor(Color.RED);
                    gra.drawString("Don\'t mind",250-(metrics.stringWidth("Don\'t mind"))/2,400);
                    java.io.FileReader fr=null;
                    java.io.BufferedReader br1 = null;
                    int backscore = 0;
                    try {

                    	fr = new java.io.FileReader("score.txt");
                    	br1 = new java.io.BufferedReader(fr);
                    	backscore = Integer.parseInt(br1.readLine());
                    	br1.close();
                    } catch (IOException e1) {
                    	// TODO Auto-generated catch block
                    	e1.printStackTrace();
                    }


				    if(score>backscore){
				    	try {
				    		java.io.FileWriter fw = new java.io.FileWriter("score.txt");
				    		fw.write(String.valueOf(score));
				    		fw.close();
				    	}catch (IOException e) {
				    		JOptionPane.showMessageDialog(shootingFrame, "ハイスコアの保存に失敗しました");
				    	}
				    }
		                    gra.drawString("Score:"+score, 250 - (metrics.stringWidth("Score:"+score) / 2), 150);
		                    gra.drawString("Press ESC to Return Start Screen", 250 - (metrics.stringWidth("Press ESC to Return Start Screen") / 2), 200);
		                    if(Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
		                        screen = EnumShootingScreen.START;
		                    }
		                    break;
            }

            gra.setColor(Color.BLACK);
            gra.setFont(new Font("SansSerif", Font.PLAIN, 10));
            gra.drawString(FPS + "FPS", 0, 470);

            shootingFrame.panel.draw();

            try {
                long runTime = System.currentTimeMillis() - startTime;
                if(runTime<(1000 / fps)) {
                    Thread.sleep((1000 / fps) - (runTime));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	public static Clip createClip(File path) {
		//�w�肳�ꂽURL�̃I�[�f�B�I���̓X�g���[�����擾
		try (AudioInputStream ais = AudioSystem.getAudioInputStream(path)){

			//�t�@�C���̌`���擾
			AudioFormat af = ais.getFormat();

			//�P���̃I�[�f�B�I�`�����܂ގw�肵�����񂩂��f�[�^���C���̏����I�u�W�F�N�g���\�z
			DataLine.Info dataLine = new DataLine.Info(Clip.class,af);

			//�w�肳�ꂽ Line.Info �I�u�W�F�N�g�̋L�q�Ɉ��v���郉�C�����擾
			Clip c = (Clip)AudioSystem.getLine(dataLine);

			//�Đ���������
			c.open(ais);

			return c;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
	}

}
