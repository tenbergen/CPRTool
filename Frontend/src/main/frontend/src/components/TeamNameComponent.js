import React, { useState } from 'react';
import axios from 'axios';

const TeamNameComponent = () => {
  const [formData, setFormData] = useState({
    nameA: '',
    nameB: '',
    yearA: '',
    yearB: '',
  });

  const { nameA, nameB, yearA, yearB } = formData;

  const [teamName, setTeamName] = useState('Nothing to show here!');
  const url = 'http://localhost:9085/teamnames/addteamname';

  const OnChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = {
      nameA: nameA,
      nameB: nameB,
      yearA: yearA,
      yearB: yearB,
    };
    console.log(data);
    await axios.post(url, data);
    await getTeamName();
  };

  const getTeamName = async () => {
    const res = await axios.get(url);
    setTeamName('All teamnames: ' + res.data);
  };

  return (
    <div>
      <div>
        <h1>GUI and Engine Test App </h1>
        <label>Name A </label>
        <input
          type='text'
          value={nameA}
          name='nameA'
          onChange={(e) => OnChange(e)}
        />
        <br />

        <label>Name B</label>
        <input
          type='text'
          value={nameB}
          name='nameB'
          onChange={(e) => OnChange(e)}
        />
        <br />

        <label>Year A</label>
        <input
          type='text'
          value={yearA}
          name='yearA'
          onChange={(e) => OnChange(e)}
        />
        <br />

        <label>Year B</label>
        <input
          type='text'
          value={yearB}
          name='yearB'
          onChange={(e) => OnChange(e)}
        />

        <br />
        <button type='button' onClick={handleSubmit}>
          Submit Team Name
        </button>
        <br />
      </div>

      <textarea value={teamName} />
    </div>
  );
};

export default TeamNameComponent;
