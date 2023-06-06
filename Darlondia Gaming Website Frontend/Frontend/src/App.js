import "./App.css";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./Pages/Home";
import Login from "./Pages/Login";
import Header from "./components/Header";
import RegisterEndUser from "./Pages/RegisterEndUser";
import ResetPassword from "./Pages/ResetPassword";
import UserProfile from "./Pages/UserProfile";
import EditUsername from "./Pages/EditUsername";
import EditEmail from "./Pages/EditEmail";
import EditBio from "./Pages/EditBio";
import GameList from "./Pages/GameList";
import ForgotPassword from "./Pages/ForgotPassword";
import GameDetail from "./Pages/GameDetail";
import EditProfilePicture from "./Pages/EditProfilePicture";
import AllLists from "./Pages/AllLists";
import UserGameLists from "./Pages/UserGameLists";
import UserList from "./Pages/UserList";
import ReviewForm from "./Pages/ReviewForm";
import UserDetail from "./Pages/UserDetail";
import CreateList from "./Pages/CreateList";
import ListDetail from "./Pages/ListDetail";
import AddToListForm from "./Pages/AddToListForm";
import SearchResultsPage from "./Pages/SearchResultsPage";

function App() {
  return (
    <Router>
      <div className="App">
        <Header />

        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/GameList" element={<GameList />} />
          <Route path="/games/:id" element={<GameDetail />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registerenduser" element={<RegisterEndUser />} />
          <Route path="/resetpassword" element={<ResetPassword />} />
          <Route path="/forgotpassword" element={<ForgotPassword />} />
          <Route path="/userprofile" element={<UserProfile />} />
          <Route path="/editusername" element={<EditUsername />} />
          <Route path="/editemail" element={<EditEmail />} />
          <Route path="/editbio" element={<EditBio />} />
          <Route path="/alllists" element={<AllLists />} />
          <Route path="/createlist" element={<CreateList />} />
          <Route path="/lists/:id" element={<ListDetail />} />
          <Route path="/usergamelists" element={<UserGameLists />} />
          <Route path="/userlist" element={<UserList />} />
          <Route path="/addtolistform/:gameId" element={<AddToListForm />} />
          <Route path="/user/:username" element={<UserDetail />} />
          <Route path="/reviewform" element={<ReviewForm />} />
          <Route path="/editprofilepicture" element={<EditProfilePicture />} />
          <Route path="/search-results" element={<SearchResultsPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
