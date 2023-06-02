import React, { useState, useEffect } from 'react';
import './Feed.css'

interface Data {
  ratings: string[];
  dates: string[];
  titles: string[];
  summaries: string[];
  pictures: string[];
}

const Feed: React.FC = () => {
  const [data, setData] = useState<Data | null>(null);

  useEffect(() => {
    Promise.all([
      fetch('http://192.168.0.15:8080/init'),
      fetch('http://192.168.0.15:8080/pictures')
    ])
      .then(([initResponse, picturesResponse]) => Promise.all([initResponse.json(), picturesResponse.json()]))
      .then(([initData, picturesData]) => setData({ ...initData, pictures: picturesData.pictures }));
  }, []);

const handleButtonClick = (title: string) => {
  window.open(`http://192.168.0.15:8080/article?title=${title}`);
};

  return (
    <div className="feed">
      {/* Add your Feed content here */}
      {data && data.titles.map((title, index) => (
        <button key={index} onClick={() => handleButtonClick(title)}>
          <div className="background-image" style={{ backgroundImage: `url(data:image/jpeg;base64,${data.pictures[index]})` }} />
          <div className="content">
            <div className="title">{title}</div>
            <div className="summary">{data.summaries[index]}</div>
            <div className="details">
              <div>Date: {data.dates[index]}</div>
              <div>Rating: {data.ratings[index]}</div>
            </div>
          </div>
        </button>
      ))}
    </div>
  );
};

export default Feed;
