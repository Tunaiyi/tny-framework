package drama.actor;

public interface AnswerListener<V> {

	void answerWith(Answer<V> answer, V value, Throwable reason);

}
