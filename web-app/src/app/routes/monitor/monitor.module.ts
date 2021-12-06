import { NgModule, Type } from '@angular/core';
import { SharedModule } from '@shared';
import { MonitorRoutingModule } from './monitor-routing.module';
import {MonitorNewComponent} from "./monitor-new/monitor-new.component";
import {MonitorEditComponent} from "./monitor-edit/monitor-edit.component";
import {MonitorListComponent} from "./monitor-list/monitor-list.component";
import {MonitorDetailComponent} from "./monitor-detail/monitor-detail.component";
import {NzBreadCrumbModule} from "ng-zorro-antd/breadcrumb";
import {NzDividerModule} from "ng-zorro-antd/divider";
import {NzSwitchModule} from "ng-zorro-antd/switch";
import {NzTagModule} from "ng-zorro-antd/tag";
import {NzRadioModule} from "ng-zorro-antd/radio";
import {NgxEchartsModule} from "ngx-echarts";
import {NzLayoutModule} from "ng-zorro-antd/layout";
import {NzSpaceModule} from "ng-zorro-antd/space";
import {MonitorDataChartComponent} from "./monitor-data-chart/monitor-data-chart.component";

const COMPONENTS: Type<void>[] = [
  MonitorNewComponent,
  MonitorEditComponent,
  MonitorListComponent,
  MonitorDetailComponent,
  MonitorDataChartComponent
];

@NgModule({
  imports: [
    SharedModule,
    MonitorRoutingModule,
    NzBreadCrumbModule,
    NzDividerModule,
    NzSwitchModule,
    NzTagModule,
    NzRadioModule,
    NgxEchartsModule,
    NzLayoutModule,
    NzSpaceModule
  ],
  declarations: COMPONENTS,
})
export class MonitorModule { }
