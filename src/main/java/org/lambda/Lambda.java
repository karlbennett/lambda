package org.lambda;

/**
 * Lambda interface that should be implemented to inject method logic into another method to simulate an anonymous
 * function.
 * @param <R> the return type of the lambda method.
 * @param <A> the argument type of the lambda method.
 */
public interface Lambda<R, A> {

    /**
     * Is run from within any method that contains the Lambda class as an argument. Override and place custom logic
     * within.
     * @param l the argument/s that will be passed from within the outer method.
     * @return the result of your logic back to outer method.
     */
    public R lambda(A... l);
}
