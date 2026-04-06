// utils/memberUtils.js
export function groupByUserId(list, idKey) {
  const grouped = list.reduce((acc, cur) => {
    if (!acc[cur[idKey]]) {
      acc[cur[idKey]] = {
        ...cur,
        roles: []
      };
      delete acc[cur[idKey]].roleId;
      delete acc[cur[idKey]].roleName;
      delete acc[cur[idKey]].isInherited;
    }
    acc[cur[idKey]].roles.push({
      roleId: cur.roleId,
      roleName: cur.roleName,
      isInherited: cur.isInherited
    });
    return acc;
  }, {});
  return Object.values(grouped);
}
