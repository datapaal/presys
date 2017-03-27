export const isEmpty = object => Object.keys(object).length === 0;

export const isEqual = (obj1, obj2) => JSON.stringify(obj1) === JSON.stringify(obj2);
