public enum OpenAIModel {
  GPT_4_1_NANO("gpt-4.1-nano"),
  GPT_4("gpt-4"),
  GPT_4_TURBO("gpt-4-turbo"),
  GPT_4O("gpt-4o"),
  GPT_4O_2024_11_20("gpt-4o-2024-11-20"),
  DAVINCI_002("davinci-002"),
  BABBAGE_002("babbage-002"),
  GPT_4_1("gpt-4.1"),
  GPT_4_5_PREVIEW("gpt-4.5-preview");

  private final String modelName;

  OpenAIModel(String modelName) {
    this.modelName = modelName;
  }

  public String getModelName() {
    return modelName;
  }

  @Override
  public String toString() {
    return modelName;
  }
}
