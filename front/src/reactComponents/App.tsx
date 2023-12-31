/**

 Root component for the application's routing.
 @return {JSX.Element} The JSX element representing the root component.
 */
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Home from "./pages/home";
import About from "./pages/about";
import NotFound from "./pages/NotFound";
import WorkoutCalendar from "./elements/calendar";
import Register from "./pages/register";
import SignIn from "./pages/signin";
import WorkoutDisplay from "./pages/workoutDisplay";
import CreateNewSchedule from "./pages/createNewSchedule";

function App() {
  return (
    <Router aria-label="Application routing">
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/calendar" element={<WorkoutCalendar />} />
        <Route path="*" element={<NotFound />} />
        <Route path="/signin" element={<SignIn />} />
        <Route path="/register" element={<Register />} />
        <Route path="/plan" element={<WorkoutDisplay />} />
        <Route path="/create-plan" element={<CreateNewSchedule />} />
      </Routes>
    </Router>
  );
}

export default App;
