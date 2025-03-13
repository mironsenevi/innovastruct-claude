// Language: JavaScript (JSX)
import React from "react";

const Card = ({ title, content }) => {
  return (
    <div className="bg-white rounded-lg shadow p-6 hover:shadow-lg">
      <h2 className="text-xl font-semibold mb-4">{title}</h2>
      <p>{content}</p>
    </div>
  );
};

export default Card;