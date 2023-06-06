import './App.css';
import { useState } from "react";
import Axios from 'axios';
/* Just a heads up */

function App() {
    
    
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [Status, setStatus] = useState("");



    const addUser = () => {
        Axios.post('http://localhost:3001/register_end_user', {
            username: username,
            password: password
        }).then((response) => {
            if(response.data.message){
                setStatus(response.data.message);
            }
            
        });

    };



    return (
        <div className="App">

            
            <div className="information">
                <h3>Register</h3> 
                <h3>{Status}</h3>
                <label> Username:</label>
                <input type="text" name="username" id="username" onChange={(e) => { setUsername(e.target.value) }} />
                <label> Password:</label>
                <input type="password" onChange={(e) => { setPassword(e.target.value) }} />
                <button onClick={addUser}>Register</button>
            </div>
        </div>
    );
}

export default App;
