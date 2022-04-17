package com.tny.game.expr;

public class ConstNumberExpr extends MapExpr {

	public ConstNumberExpr(Number number) {
		super(number);
	}

	public ConstNumberExpr(ConstNumberExpr expr) {
		super(expr.number);
	}

	@Override
	protected Object execute() throws Exception {
		return number;
	}

	@Override
	public Expr createExpr() {
		return new ConstNumberExpr(this);
	}

}
