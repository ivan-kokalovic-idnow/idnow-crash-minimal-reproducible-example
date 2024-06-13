import React from 'react';
import {SafeAreaView, NativeModules, Button, TextInput} from 'react-native';



const RNIdNow = NativeModules.RNIdNow;

function App() {
  const [transactionToken, setTransactionToken] = React.useState('TST-ZZLHS');
  const settings = {
    companyId: 'solarisbankvideoidentbison',
    environment: 'test',
    showErrorSuccessScreen: false,
    showVideoOverviewCheck: true,
    transactionToken: 'TST-ZZLHS',
  };


  const startIdNow = async () => {
    try {
      const response = await RNIdNow.start(settings);
      return { response };
    } catch (error: any) {
      console.error('Error with sdk', error);
    }
  };



  return (
    <SafeAreaView style={{flex: 1, justifyContent: 'center'}}>
       <TextInput
        placeholder="Enter Transaction Token"
        value={transactionToken}
        onChangeText={setTransactionToken}
      />
      <Button title={'Start IDNow'} onPress={startIdNow} />
    </SafeAreaView>
  );
}

export default App;
