import React from 'react';
import {SafeAreaView, NativeModules, Button} from 'react-native';

const RNIdNow = NativeModules.RNIdNow;

function App() {
  const settings = {
    companyId: 'solarisbankvideoidentbison',
    environment: 'test',
    showErrorSuccessScreen: false,
    showVideoOverviewCheck: true,
    transactionToken: 'TST-YMUQE',
  };

  const startIdNow = async () => {
    const response = await RNIdNow.start(settings);
    return {response};
  };

  return (
    <SafeAreaView style={{flex: 1, justifyContent: 'center'}}>
      <Button title={'Start IDNow'} onPress={startIdNow} />
    </SafeAreaView>
  );
}

export default App;
