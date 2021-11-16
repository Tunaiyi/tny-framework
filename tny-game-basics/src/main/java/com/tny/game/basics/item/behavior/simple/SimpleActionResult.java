package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.behavior.*;

import java.util.*;

/**
 * 操作行为结果
 *
 * @author KGTny
 */
public class SimpleActionResult implements ActionResult {

    public Action action;

    /**
     * 该操作的条件结果
     */
    private List<DemandResult> demandResult;

    /**
     * 该操作的消耗条件结果
     */
    private List<DemandResult> costDemandResult;

    /**
     * 奖励列表
     */
    private AwardList awardList;

    public SimpleActionResult(Action action, List<DemandResult> demandResult, List<DemandResult> costDemandResult, AwardList awardList) {
        super();
        this.action = action;
        this.demandResult = Collections.unmodifiableList(demandResult);
        this.costDemandResult = Collections.unmodifiableList(costDemandResult);
        this.awardList = awardList;
    }

    public SimpleActionResult(Action action, List<DemandResult> resultList, ActionResult actionResult) {
        this.action = action;
        List<DemandResult> demandResult = null;
        if (resultList.isEmpty()) {
            demandResult = actionResult.getDemandResultList();
        } else {
            demandResult = new ArrayList<DemandResult>();
            demandResult.addAll(resultList);
            demandResult.addAll(actionResult.getDemandResultList());
            demandResult = Collections.unmodifiableList(demandResult);
        }
        this.demandResult = Collections.unmodifiableList(demandResult);
        this.costDemandResult = actionResult.getCostDemandResultList();
        this.awardList = actionResult.getAwardList();
    }

    @Override
    public List<DemandResult> getDemandResultList() {
        return demandResult;
    }

    @Override
    public List<DemandResult> getCostDemandResultList() {
        return costDemandResult;
    }

    @Override
    public AwardList getAwardList() {
        return awardList;
    }

    @Override
    public Action getAction() {
        return action;
    }

}