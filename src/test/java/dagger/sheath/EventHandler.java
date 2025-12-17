package dagger.sheath;

public interface EventHandler<T> {
  /**
   * Called after inject mocks and spies on Dagger component and Junit instance fields.
   * This method will be called once per test class as only one Dagger instance
   * will be created per test class.
   *
   * @param component Dagger created component.
   */
  default void afterSetup(T component) {
  }

  /**
   * On after all junit event.
   */
  default void afterAll(T component) {
  }
}
