<#-- @ftlvariable name="devices" type="kotlin.collections.List<com.corrot.db.data.model.Device>" -->
<#setting time_zone="GMT">
<!DOCTYPE html>
<html>

<style>
  table.minimalistBlack {
    border: 3px solid #000000;
    text-align: center;
    border-collapse: collapse;
    margin-left: auto;
    margin-right: auto;
  }

  table.minimalistBlack td,
  table.minimalistBlack th {
    border: 1px solid #000000;
    padding: 15px 15px;
  }

  table.minimalistBlack tbody td {
    font-size: 15px;
  }

  table.minimalistBlack thead {
    background: #CFCFCF;
    background: -moz-linear-gradient(top, #dbdbdb 0%, #d3d3d3 66%, #CFCFCF 100%);
    background: -webkit-linear-gradient(top, #dbdbdb 0%, #d3d3d3 66%, #CFCFCF 100%);
    background: linear-gradient(to bottom, #dbdbdb 0%, #d3d3d3 66%, #CFCFCF 100%);
    border-bottom: 3px solid #000000;
  }

  table.minimalistBlack thead th {
    font-size: 15px;
    font-weight: bold;
    color: #000000;
    text-align: center;
  }

  table.minimalistBlack tfoot td {
    font-size: 14px;
  }
</style>


<body style="text-align: center; font-family: sans-serif">
  <h1>Kwiatonomous</h1>
  <p>
    <i>All devices</i>
  </p>
  <hr>
  <table class="minimalistBlack">
    <tr>
      <th style="background-color: silver">Active</th>
      <th style="background-color: silver">Device ID</th>
      <th style="background-color: silver">Last updated</th>
    </tr>
    <#list devices?reverse as device>
      <tr>
        <td>
          <#if (((((.now?long / 1000.0) - device.lastUpdate) / 60.0)) <= 30.0)>
            <span>&#128994;</span>
          <#else>
            <span>&#128308;</span>
          </#if>
        </td>
        <td>${device.deviceId}</td>
        <td>${((device.lastUpdate*1000))?number_to_datetime}</td>
      </tr>
    </#list>
  </table>
  <hr>
  <button onClick="window.location.reload();">Refresh Page</button>
  <p>(last refreshed ${.now?string.medium})</p>
</body>
</html>