package com.tny.game.basics.item;

import com.tny.game.expr.*;

/**
 * Created by Kun Yang on 2018/6/4.
 */
public class GameItemModelContext implements ItemModelContext {

	private final ItemExplorer itemExplorer;

	private final ModelExplorer itemModelExplorer;

	private final ExprHolderFactory exprHolderFactory;

	public GameItemModelContext(GameExplorer gameExplorer,
			ExprHolderFactory exprHolderFactory) {
		this(gameExplorer, gameExplorer, exprHolderFactory);
	}

	public GameItemModelContext(ItemExplorer itemExplorer,
			ModelExplorer itemModelExplorer,
			ExprHolderFactory exprHolderFactory) {
		this.itemExplorer = itemExplorer;
		this.itemModelExplorer = itemModelExplorer;
		this.exprHolderFactory = exprHolderFactory;
	}

	@Override
	public ItemExplorer getItemExplorer() {
		return itemExplorer;
	}

	@Override
	public ModelExplorer getItemModelExplorer() {
		return itemModelExplorer;
	}

	@Override
	public ExprHolderFactory getExprHolderFactory() {
		return exprHolderFactory;
	}

}
