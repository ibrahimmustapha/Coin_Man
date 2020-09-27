package com.coin.man.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bgColor;
	Texture coins;
	Texture man[];
	Texture dizzy;
	int manState = 0;
	int manY = 0;
	int pause = 0;
	int coinsCount;
	int bombCount = 0;
	int score;
	int gameState = 0;
	Texture bombs;
	float gravity = 0.2f;
	float velocity = 0;
	BitmapFont font;
	Random random;
	Rectangle manRectangle;
	ArrayList<Rectangle> coinsRectnagle = new ArrayList<>();
	ArrayList<Rectangle> bombsRectnagle = new ArrayList<>();
	ArrayList<Integer> coinsX = new ArrayList<>();
	ArrayList<Integer> coinsY = new ArrayList<>();
	ArrayList<Integer> bombX = new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	String gameOver = "GAME_OVER";
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		bgColor = new Texture("bg.png");
		dizzy = new Texture("dizzy-1.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(8);
		random = new Random();
		manRectangle = new Rectangle();
		coins = new Texture("coin.png");
		bombs = new Texture("bomb.png");
	}

	public void coinsShow() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinsX.add((int)height);
		coinsY.add(Gdx.graphics.getWidth());
	}

	public void bombsShow() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombX.add((int)height);
		bombY.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();

		batch.draw(bgColor, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState ==  1) {
			//
			if (bombCount < 500) {
				bombCount++;
			} else {
				bombCount = 0;
				bombsShow();
			}

			bombsRectnagle.clear();

			for (int i=0; i < bombX.size(); i++) {
				batch.draw(bombs, bombX.get(i), bombY.get(i));
				bombX.set(i, bombX.get(i) - 8);
				bombsRectnagle.add(new Rectangle(bombX.get(i), bombY.get(i), bombs.getWidth(), bombs.getHeight()));
			}

			if (coinsCount < 100) {
				coinsCount++;
			} else {
				coinsCount = 0;
				coinsShow();
			}

			coinsRectnagle.clear();

			for (int i=0; i < coinsX.size(); i++) {
				batch.draw(coins, coinsX.get(i), coinsY.get(i));
				coinsX.set(i, coinsX.get(i) - 4);
				coinsRectnagle.add(new Rectangle(coinsX.get(i), coinsY.get(i), coins.getWidth(), coins.getHeight()));
			}

			if (Gdx.input.justTouched()) {
				velocity = -10;
			}

			if (pause < 4) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if (manY <= 0) {
				manY = 0;
			}

		} else if (gameState == 0) {
			// Waiting to start
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2){
			// Game Over
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getWidth() / 2;
				score = 0;
				velocity = 0;
				coinsX.clear();
				coinsY.clear();
				coinsRectnagle.clear();
				coinsCount = 0;
				bombX.clear();
				bombY.clear();
				bombsRectnagle.clear();
				bombCount = 0;
			}
		}

		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
			font.draw(batch, gameOver, 100, 200);
		} else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		for (int i=0; i < coinsRectnagle.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinsRectnagle.get(i))) {
				score++;

				coinsRectnagle.remove(i);
				coinsX.remove(i);
				coinsY.remove(i);
				break;
			}
		}

		for (int i=0; i < bombsRectnagle.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombsRectnagle.get(i))) {
				Gdx.app.log("Bombs: ", "Collision");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
