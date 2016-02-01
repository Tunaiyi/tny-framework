package com.tny.game.base.item;

public interface RandomCreatorFactory {

    String getName();

    <P extends Probability> RandomCreator<P> getRandomCreator();

}
