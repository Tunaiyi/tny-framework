package test;

@SuppressWarnings("rawtypes")
public enum KeyType implements KeyModel {

    DATA {
        @Override
        public KeyType getModel() {
            return null;
        }

    };

}
