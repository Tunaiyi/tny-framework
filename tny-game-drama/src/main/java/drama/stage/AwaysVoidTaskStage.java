package drama.stage;

/**
 * Created by Kun Yang on 16/1/22.
 */
class AwaysVoidTaskStage extends BaseTaskStage<Void> {

    protected TaskFragment<Object, Object> fragment;

    @SuppressWarnings("unchecked")
    public AwaysVoidTaskStage(CommonTaskStage head, TaskFragment<?, ?> fragment) {
        super(head);
        this.fragment = (TaskFragment<Object, Object>) fragment;
    }

    @Override
    public TaskFragment<?, ?> getTaskFragment() {
        return fragment;
    }


}
